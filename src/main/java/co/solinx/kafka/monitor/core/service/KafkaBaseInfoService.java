package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.common.KafkaUtils;
import co.solinx.kafka.monitor.core.listener.BrokerListener;
import co.solinx.kafka.monitor.core.listener.TopicListener;
import co.solinx.kafka.monitor.model.*;
import co.solinx.kafka.monitor.utils.JsonLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kafka.api.PartitionMetadata;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.client.ClientUtils;
import kafka.client.ClientUtils$;
import kafka.cluster.BrokerEndPoint;
import kafka.cluster.Replica;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.network.BlockingChannel;
import kafka.utils.ZkUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.*;
import scala.collection.convert.*;
import scala.collection.mutable.ArraySeq;

import java.io.InputStream;
import java.util.*;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_ADDED;

/**
 * 缓存kafka基本信息(broker、topic,consumer)
 * brokerCache <brokerId,Broker>
 * topicCache topicList
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/12.
 */
public class KafkaBaseInfoService {

    private Logger logger = LoggerFactory.getLogger(KafkaBaseInfoService.class);
    private CuratorFramework curator = CuratorService.getInstance();
    /**
     * topic节点缓存
     */
    private TreeCache topicTreeCache;
    /**
     * consumer缓存
     */
    private TreeCache consumerTreeCache;
    /**
     * broker节点缓存
     */
    private PathChildrenCache brokerPathCache;
    /**
     * topic配置缓存
     */
    private PathChildrenCache topicConfigCache;

    /**
     * broker信息缓存
     */
    public Map<Integer, Broker> brokerCache = new TreeMap();

    private List<Topic> topicList = new ArrayList<>();
    /**
     * 集群控制节点
     */
    private Controller controller;
    /**
     * controller节点缓存
     */
    private NodeCache controllerNodeCache;

    private static KafkaBaseInfoService kafkaService = null;

    public synchronized static KafkaBaseInfoService getInstance() {
        if (kafkaService == null) {
            kafkaService = new KafkaBaseInfoService();
        }
        return kafkaService;
    }

    private KafkaBaseInfoService() {

        try {

            brokerPathCache = new PathChildrenCache(curator, ZkUtils.BrokerIdsPath(), true);
            brokerPathCache.getListenable().addListener(new BrokerListener(brokerCache, brokerPathCache));
            brokerPathCache.start();

            topicConfigCache = new PathChildrenCache(curator, ZkUtils.EntityConfigPath() + "/topics", true);
            topicConfigCache.start();

            //topic缓存
            topicTreeCache = new TreeCache(curator, ZkUtils.BrokerTopicsPath());
            topicTreeCache.getListenable().addListener(new TopicListener(topicList));
            topicTreeCache.start();

            consumerTreeCache = new TreeCache(curator, ZkUtils.ConsumersPath());
            consumerTreeCache.start();


            //controller信息
            controllerNodeCache = new NodeCache(curator, ZkUtils.ControllerPath());
            controllerNodeCache.getListenable().addListener(() -> controller = JSON.parseObject(controllerNodeCache.getCurrentData().getData(), Controller.class));
            controllerNodeCache.start(true);
            controller = JSON.parseObject(controllerNodeCache.getCurrentData().getData(), Controller.class);

            //todo 等待缓存zk数据
            Thread.sleep(5000);
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    /**
     * 从缓存topic基础信息中取得replicas、isr、leader
     *
     * @param topic
     */
    private void mergeTopic(Topic topic) {
        Topic tm = topicList.stream().filter(m -> m.getName().equals(topic.getName())).findFirst().get();
        topic.getPartitionMap().values().stream().forEach((t) -> {
            if (t.getLeaderId() == -1) {
                Partition partition = tm.getPartitionMap().get(t.getId());
                t.setReplicasArray(partition.getReplicasArray());
                t.setIsr(partition.getIsr());
                t.setLeader(partition.getLeaderId());
            }
        });
    }

    /**
     * 取得所有Topic
     *
     * @return
     */
    public List<Topic> getTopics() {
        //todo 处理topic具体信息
        return getTopicMetadata().values().stream().sorted(Comparator.comparing(Topic::getName)).map((e) -> {

            Map<Integer, Partition> partitionMap = e.getPartitionMap();
            mergeTopic(e);
            double preferred = 0;
            int underReplicated = 0;
            int partitionSize = partitionMap.size();
            //计算首选副本率
            for (Partition tPart :
                    partitionMap.values()) {
                if (tPart.isLeaderPreferred()) {
                    preferred++;
                }
                int rSum = tPart.getReplicas().size();
                if (rSum > 0) {
                    if (tPart.getIsr().length < rSum) {
                        underReplicated++;
                    }
                } else {
                    underReplicated++;
                }
            }
            e.setPreferred(preferred / partitionSize * 100);

            e.setUnderReplicated(underReplicated);
            return e;
        }).collect(Collectors.toList());


    }

    /**
     * 取得topic
     *
     * @param topic
     * @return
     */
    public Topic getTopic(String topic) {
        final Topic resultTopic = getTopicMetadata(topic).get(topic);
        //lastOffset
        getTopicPartitionSizes(resultTopic, kafka.api.OffsetRequest.LatestTime()).entrySet()
                .forEach(entry -> {
                    Partition tp = resultTopic.getPartition(entry.getKey());
                    if (tp != null) {
                        tp.setSize(entry.getValue());
                    }
                });
        //firstOffset
        getTopicPartitionSizes(resultTopic, kafka.api.OffsetRequest.EarliestTime()).entrySet()
                .forEach(entry -> {
                    Partition tp = resultTopic.getPartition(entry.getKey());
                    if (tp != null) {
                        tp.setFirstOffset(entry.getValue());
                    }
                });
        mergeTopic(resultTopic);

        return resultTopic;
    }

    /**
     * 取topic中partition的偏移(offset)
     *
     * @param topic topic
     * @param time  偏移时间
     * @return
     */
    private Map<Integer, Long> getTopicPartitionSizes(Topic topic, long time) {

        return topic.getPartitionMap().values().parallelStream()
                .filter(p -> p.getLeader() != null)
                .collect(Collectors.groupingBy(p -> p.getLeader().getId()))
                .entrySet().parallelStream()
                .map(entry -> {
                    final Integer brokerID = entry.getKey();
                    final List<Partition> brokerPartitions = entry.getValue();
                    try {
                        Broker broker = getBrokerById(brokerID);
                        //partition 偏移(offset) 请求信息
                        OffsetResponse offsetResponse = KafkaUtils.sendOffsetRequest(broker, topic, brokerPartitions, time);

                        return brokerPartitions.stream()
                                .collect(Collectors.toMap(Partition::getId,
                                        partition -> Optional.ofNullable(
                                                offsetResponse.offsets(topic.getName(), partition.getId()))
                                                .map(Arrays::stream)
                                                .orElse(LongStream.empty())
                                                .findFirst()
                                                .orElse(-1L)
                                ));
                    } catch (Exception ex) {
                        logger.error("Unable to get partition log size for topic {} partitions {}", topic.getName(),
                                brokerPartitions.stream()
                                        .map(Partition::getId)
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(",")));
                        ex.printStackTrace();
                        return brokerPartitions.stream().collect(Collectors.toMap(Partition::getId, tp -> -1L));
                    }
                })
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    /**
     * 使用brokerId取broker
     *
     * @param brokerID
     * @return
     */
    public Broker getBrokerById(int brokerID) {
        if (brokerCache.size() == 0) {
            throw new RuntimeException("no broker available ");

        }
        return brokerCache.get(brokerID);
    }

    /**
     * 取得topic元数据
     *
     * @param topics
     * @return
     */
    public Map<String, Topic> getTopicMetadata(String... topics) {

        //请求topic元数据
        kafka.api.TopicMetadataResponse response = ClientUtils.fetchTopicMetadata(JavaConversions.asScalaIterable(Arrays.asList(topics)).toSet(), JavaConversions.asScalaBuffer(getBrokerEndPoints()), "test", 2000, 1);

        //从元数据中取得topic信息
        Map<String, Topic> topicMap = WrapAsJava$.MODULE$.seqAsJavaList(response.topicsMetadata())
                .stream().filter(error -> error.errorCode() == ErrorMapping.NoError())
                .map((temp) -> {
                    Topic topic = new Topic(temp.topic());
                    topic.setConfig(JSONObject.parseObject(topicConfigCache.getCurrentData(ZkUtils.EntityConfigPath() + "/topics/" + temp.topic()).getData(), Map.class));
                    List<PartitionMetadata> pMetadata = WrapAsJava$.MODULE$.seqAsJavaList(temp.partitionsMetadata());
                    topic.setPartitionMap(
                            pMetadata.stream()
                                    .map((pMta) -> {
                                        //添加Partition副本信息
                                        Partition partition = new Partition(pMta.partitionId());
                                        BrokerEndPoint leader;
                                        int leaderId = -1;
                                        if (pMta.leader().nonEmpty()) {
                                            leader = pMta.leader().get();
                                            leaderId = leader.id();
                                        }

                                        partition.setIsr(WrapAsJava$.MODULE$.seqAsJavaList(pMta.isr()).stream().mapToInt(i -> i.id()).toArray());


                                        for (BrokerEndPoint replica :
                                                WrapAsJava$.MODULE$.seqAsJavaList(pMta.replicas())) {
                                            boolean isLeader = false;
                                            if (replica.id() == leaderId) {
                                                isLeader = true;
                                            }
                                            partition.addReplica(new PartitionReplica(replica.id(), true, isLeader));
                                        }

                                        partition.setReplicasArray(WrapAsJava$.MODULE$.seqAsJavaList(pMta.replicas()).stream().mapToInt(m -> m.id()).toArray());

                                        if (pMta.replicas().size() > 0) {
                                            //首选副本
                                            BrokerEndPoint preferedReplica = WrapAsJava$.MODULE$.seqAsJavaList(pMta.replicas()).get(0);
                                            //首选副本等于leader
                                            if (leaderId == preferedReplica.id()) {
                                                partition.setPreferredLeaderId(leaderId);
                                            }
                                        }
                                        return partition;
                                    }).collect(Collectors.toMap(Partition::getId, p -> p))
                    );
                    return topic;
                }).collect(Collectors.toMap(Topic::getName, t -> t));

        return topicMap;
    }

    private List<Integer> getIsr(String topic, PartitionMetadata pmd) {
//        return pmd.isr().stream().map((temp) -> temp.id()).collect(Collectors.toList());
        return null;
    }


    /**
     * broker列表
     *
     * @return
     */
    public List<Broker> getBrokers() {
        return brokerCache.values().stream().map((t) -> {
            if (t.getId() == controller.getBrokerId()) {
                t.setController(true);
            }
            return t;
        }).collect(Collectors.toList());
    }

    public List<BrokerEndPoint> getBrokerEndPoints() {
        List<BrokerEndPoint> endPointList = new ArrayList<>();
        brokerCache.values().stream().forEach(b -> {
            BrokerEndPoint endPoint = new BrokerEndPoint(b.getId(), b.getHost(), b.getPort());
            endPointList.add(endPoint);
        });
        return endPointList;
    }


    public Broker randomBroker() {
        int brokerId = new Random().nextInt(brokerCache.size());
        return (Broker) brokerCache.values().toArray()[brokerId];
    }

    public String reandomBrokerHost() {
        Broker broker = randomBroker();
        String host = broker.getHost();
        int port = broker.getPort();
        return String.format("%s:%d", host, port);
    }


}
