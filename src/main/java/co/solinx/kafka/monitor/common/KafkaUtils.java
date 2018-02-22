package co.solinx.kafka.monitor.common;

import co.solinx.kafka.monitor.core.service.ConfigService;
import co.solinx.kafka.monitor.model.Broker;
import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.model.Topic;
import co.solinx.kafka.monitor.model.ZooConfig;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.network.BlockingChannel;
import kafka.utils.ZKConfig;
import kafka.utils.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * kafka工具类
 *
 * @author linx
 * @create 2018-01-28 21:56
 **/
public class KafkaUtils {

    private static Logger logger = LoggerFactory.getLogger(KafkaUtils.class);

    public static ZkUtils getZkUtils() {
        ZooConfig zooConfig = ConfigService.zooConfig;
        String ip = zooConfig.getHost();
        int sessionTimeout = Integer.parseInt(zooConfig.getSessionTimeoutMs());
        int connTimeout = Integer.valueOf(zooConfig.getConnectionTimeoutMs());
        return ZkUtils.apply(ip, sessionTimeout, connTimeout, false);
    }

    /**
     * 取Broker Channel通道
     *
     * @param broker
     * @return
     */
    public static BlockingChannel getChannel(Broker broker) {
        BlockingChannel channel = new BlockingChannel(broker.getHost(),
                broker.getPort(), BlockingChannel.UseDefaultBufferSize(),
                BlockingChannel.UseDefaultBufferSize(), 10000);

        channel.connect();
        return channel;
    }

    public TopicMetadataResponse topicMetadataRequest(BlockingChannel channel, String[] topics) {
        TopicMetadataRequest request = new TopicMetadataRequest((short) 0, 0, "kafkaMonitor", Arrays.asList(topics));
        channel.send(request);
        final kafka.api.TopicMetadataResponse underlyingResponse =
                kafka.api.TopicMetadataResponse.readFrom(channel.receive().payload());
        TopicMetadataResponse response = new TopicMetadataResponse(underlyingResponse);
        return response;
    }

    /**
     * 请求取topic偏移
     *
     * @param broker
     * @param topic
     * @param brokerPartitions
     * @return
     */
    public static OffsetResponse sendOffsetRequest(Broker broker, Topic topic,
                                                   List<Partition> brokerPartitions, long time) {

        PartitionOffsetRequestInfo requestInfo = new PartitionOffsetRequestInfo(time, 1);

        final OffsetRequest offsetRequest = new OffsetRequest(
                brokerPartitions.stream()
                        .collect(Collectors.toMap(
                                partition -> new TopicAndPartition(topic.getName(), partition.getId()),
                                partition -> requestInfo)), (short) 0, "kafkaMonitor");

        logger.debug("Sending offset request: {}", offsetRequest);
        if (broker != null) {
            BlockingChannel channel = getChannel(broker);
            channel.send(offsetRequest.underlying());
            final kafka.api.OffsetResponse underlyingResponse = kafka.api.OffsetResponse.readFrom(channel.receive().payload());
            channel.disconnect();
            return new OffsetResponse(underlyingResponse);
        } else {
            return null;
        }

    }

}
