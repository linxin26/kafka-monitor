package co.solinx.kafka;

import kafka.admin.AdminClient;
import kafka.admin.ConsumerGroupCommand;
import kafka.common.TopicAndPartition;
import kafka.coordinator.GroupOverview;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Function1;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/19.
 */
public class TestKafkaConsumerGroupService implements ConsumerGroupCommand.ConsumerGroupService {

    AdminClient adminClient;
    Logger logger = LoggerFactory.getLogger(TestKafkaConsumerGroupService.class);
    org.apache.kafka.clients.consumer.KafkaConsumer consumer;

    public TestKafkaConsumerGroupService() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:9092");
        props.put("group.id", "test");
        adminClient = AdminClient.create(props);

        logger.debug("{}", adminClient.bootstrapBrokers());

    }

    @Override
    public void list() {

    }

    public List<GroupOverview> groupList() {
        return JavaConversions.asJavaList(adminClient.listAllConsumerGroupsFlattened());
    }

    @Override
    public void describe() {

    }

    @Override
    public void close() {

    }

    @Override
    public ConsumerGroupCommand.ConsumerGroupCommandOptions opts() {
        return null;
    }

    @Override
    public ConsumerGroupCommand.LogEndOffsetResult getLogEndOffset(String topic, int partition) {
        return null;
    }

    @Override
    public void describeGroup(String group) {

        List<AdminClient.ConsumerSummary> consumerSummaryList = JavaConversions.asJavaList(adminClient.describeConsumerGroup(group));

        Consumer consumer = getConsumer();
        logger.debug("consumerList  -----  {}", consumerSummaryList);

        consumerSummaryList.stream().forEach(e -> {
            List<TopicPartition> topicPartitions = JavaConversions.asJavaList(e.assignment());
            Stream<Map<String, Long>> partitionOffsets = topicPartitions.stream().flatMap(topicPartition -> {
                Map<String, Long> topic = new HashMap<>();
                OffsetAndMetadata metadata = consumer.committed(new TopicPartition(topicPartition.topic(), topicPartition.partition()));
                if(metadata!=null) {
                    topic.put(topicPartition.topic(), metadata.offset());
                    logger.debug("-------- offset {}", metadata.offset());
                }

                return Stream.of(topic);
            });
            //partitionOffsets
            // logger.debug("partitionOffsets {}", partitionOffsets.collect(Collectors.toList()));

            final Map<String, Long> partitionOffsetsMap = topicPartitions.size() > 0 ? partitionOffsets.findFirst().get() : new HashMap<>();

            topicPartitions.forEach(tp -> {
                long endOff = findLogEndOffset(tp.topic(), tp.partition());
                long currentOff = 0;
                if (partitionOffsetsMap.size() > 0)
                    currentOff = partitionOffsetsMap.get(tp.topic());
                logger.debug("{}",
                        String.format("%s %s %s %s %s %s %s %s",
                                group, tp.topic(), String.valueOf(tp.partition()),
                                currentOff, endOff, endOff - currentOff,
                                e.clientId(), e.clientHost()));
            });


        });

    }


    long findLogEndOffset(String topic, int partition) {
        Consumer consumer = getConsumer();
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        List tpList = new ArrayList();
        tpList.add(topicPartition);
        consumer.assign(tpList);
        consumer.seekToEnd(tpList);
        Long longEndOffset = consumer.position(topicPartition);
        return longEndOffset;
    }

    public Consumer getConsumer() {
        if (consumer == null) {
            consumer = newConsumer();
        }
        return consumer;
    }

    public org.apache.kafka.clients.consumer.KafkaConsumer newConsumer() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);
    }

    @Override
    public void describeTopicPartition(String group, Seq<TopicAndPartition> topicPartitions, Function1<TopicAndPartition, Option<Object>> getPartitionOffset, Function1<TopicAndPartition, Option<String>> getOwner) {

    }

    @Override
    public void printDescribeHeader() {
    }
}
