package co.solinx.kafka.monitor.core.service;

import kafka.admin.AdminClient;
import kafka.admin.ConsumerGroupCommand;
import kafka.common.TopicAndPartition;
import kafka.coordinator.GroupOverview;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Function1;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/20.
 */
public class CustomConsumerGroupService {

    private Logger logger = LoggerFactory.getLogger(CustomConsumerGroupService.class);
    private AdminClient adminClient;
    private org.apache.kafka.clients.consumer.KafkaConsumer consumer;
    Properties props = new Properties();

    public CustomConsumerGroupService() {
        props = ConfigService.getKafkaConsumerConf();
        adminClient = AdminClient.create(props);

        logger.debug("{}", adminClient.bootstrapBrokers());
    }


    /**
     * 取得所有group
     *
     * @return
     */
    public List<GroupOverview> groupList() {
        return JavaConversions.asJavaList(adminClient.listAllConsumerGroupsFlattened());
    }


    public void close() {
        adminClient.close();
    }

    public ConsumerGroupCommand.ConsumerGroupCommandOptions opts() {
        return null;
    }

    /**
     * 取得group下面所有consumer
     *
     * @param group
     * @return
     */
    public List<co.solinx.kafka.monitor.model.Consumer> getConsumerList(String group) {
        List<co.solinx.kafka.monitor.model.Consumer> consumerList = new ArrayList();
        List<AdminClient.ConsumerSummary> consumerSummaryList = JavaConversions.asJavaList(adminClient.describeConsumerGroup(group));

        Consumer consumer = getConsumer();
        consumerSummaryList.stream().forEach(consumerSummary -> {

            //取得topic与partition
            List<TopicAndPartition> topicAndPartitionStream = JavaConversions.asJavaList(consumerSummary.assignment())
                    .parallelStream().map(tp -> new TopicAndPartition(tp.topic(), tp.partition()))
                    .collect(Collectors.toList());

            /**
             * partition与偏移信息
             */
            Stream<Map<TopicAndPartition, Long>> partitionOffsets = topicAndPartitionStream.stream().flatMap(topicAndPartition -> {
                OffsetAndMetadata offsetAndMetadata = consumer.committed(new TopicPartition(topicAndPartition.topic(), topicAndPartition.partition()));
                Map<TopicAndPartition, Long> offsetMap = new HashMap<>();
                if (offsetAndMetadata != null) {
                    offsetMap.put(topicAndPartition, offsetAndMetadata.offset());
                } else {
                    offsetMap.put(topicAndPartition, -1L);
                }
                return Stream.of(offsetMap);
            });

            final Map<TopicAndPartition, Long> partitionOffsetsMap = topicAndPartitionStream.size() > 0 ? partitionOffsets.findFirst().get() : new HashMap<>();

            //取得偏移信息
            topicAndPartitionStream.forEach(topicAndPartition -> {
                co.solinx.kafka.monitor.model.Consumer model = new co.solinx.kafka.monitor.model.Consumer();
                long endOff = findLogEndOffset(topicAndPartition.topic(), topicAndPartition.partition());
                long currentOff = 0;
                if (partitionOffsetsMap.size() > 0 && partitionOffsetsMap.containsKey(topicAndPartition)) {
                    currentOff = partitionOffsetsMap.get(topicAndPartition);
                }

                model.setMemberId(consumerSummary.memberId());
                model.setCurrentOffset(currentOff);
                model.setEndOffset(endOff);
                model.setPartition(topicAndPartition.partition());
                model.setTopic(topicAndPartition.topic());
                model.setClientId(consumerSummary.clientId());
                model.setHost(consumerSummary.clientHost());
                model.setGroup(group);
                consumerList.add(model);
            });

        });
        return consumerList;
    }

    /**
     * 取得所有consumer
     *
     * @return
     */
    public List<co.solinx.kafka.monitor.model.Consumer> getConsumerList() {
        List<co.solinx.kafka.monitor.model.Consumer> consumerList = new ArrayList();
        List<GroupOverview> groupList = groupList();
        groupList.stream().forEach(group -> consumerList.addAll(getConsumerList(group.groupId())));
        return consumerList;

    }

    /**
     * 取得topic下所有consumer
     *
     * @param topic
     * @return
     */
    public List<co.solinx.kafka.monitor.model.Consumer> getConsumerByTopic(String topic) {

        return getConsumerList().stream().filter(c -> c.getTopic().equals(topic)).collect(Collectors.toList());
    }

    /**
     * 取得尾部offset（LEO）
     *
     * @param topic
     * @param partition
     * @return
     */
    private long findLogEndOffset(String topic, int partition) {
        Consumer consumer = getConsumer();
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        List tpList = new ArrayList();
        tpList.add(topicPartition);
        consumer.assign(tpList);
        consumer.seekToEnd(tpList);
        Long longEndOffset = consumer.position(topicPartition);
        return longEndOffset;
    }


    private Consumer getConsumer() {
        if (consumer == null) {
            consumer = newConsumer();
        }
        return consumer;
    }

    private org.apache.kafka.clients.consumer.KafkaConsumer newConsumer() {


        return new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);
    }

    public void describeTopicPartition(String group, Seq<TopicAndPartition> topicPartitions, Function1<TopicAndPartition, Option<Object>> getPartitionOffset, Function1<TopicAndPartition, Option<String>> getOwner) {

    }

}
