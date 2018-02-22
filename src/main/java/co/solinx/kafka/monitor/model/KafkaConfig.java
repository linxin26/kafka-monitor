package co.solinx.kafka.monitor.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * kafkaConfig
 *
 * @author linx
 * @create 2018-02 06-22:17
 **/
@Data
public class KafkaConfig {

    @JSONField(name = "bootstrap.servers")
    private String bootstrapServers = "bootstrap.servers";
    @JSONField(name = "key.deserializer")
    private String keyDeserializer = "key.deserializer";
    @JSONField(name = "value.serializer")
    private String valueSerializer = "value.serializer";
    @JSONField(name = "key.serializer")
    private String keySerializer = "key.serializer";
    @JSONField(name = "value.deserializer")
    private String valueDeserializer = "value.deserializer";
    @JSONField(name = "enable.auto.commit")
    private String enableAutoCommit = "enable.auto.commit";
    @JSONField(name = "auto.commit.interval.ms")
    private String autoCommitIntervalMs = "auto.commit.interval.ms";
    @JSONField(name = "session.timeout.ms")
    private String sessionTimeoutMs = "session.timeout.ms";
    @JSONField(name = "group.id")
    private String groupId = "group.id";
}
