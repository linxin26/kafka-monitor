package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.model.KafkaConfig;
import co.solinx.kafka.monitor.model.MonitorConfig;
import co.solinx.kafka.monitor.model.ZooConfig;
import co.solinx.kafka.monitor.utils.JsonLoader;
import com.alibaba.fastjson.JSONObject;

import java.util.Properties;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/20.
 */
public class ConfigService {


    public static JSONObject rootObj;
    public static String bootstrapServers = "bootstrap.servers";
    public static String keyDeserializer = "key.deserializer";
    public static String valueSerializer = "value.serializer";
    public static String keySerializer = "key.serializer";
    public static String valueDeserializer = "value.deserializer";
    public static String enableAutoCommit = "enable.auto.commit";
    public static String autoCommitIntervalMs = "auto.commit.interval.ms";
    public static String sessionTimeoutMs = "session.timeout.ms";
    public static String groupId = "group.id";

    private static KafkaConfig kafkaconfig;
    public static ZooConfig zooConfig;
    public static MonitorConfig monitorConfig;

    static {
        rootObj = JsonLoader.loadJSONFile(CuratorService.class.getClassLoader().getResourceAsStream("kafkaMonitorConfig.json"));
        kafkaconfig = JSONObject.parseObject(rootObj.getJSONObject("kafka").toJSONString(), KafkaConfig.class);
        zooConfig = JSONObject.parseObject(rootObj.getJSONObject("zookeeper").toJSONString(), ZooConfig.class);
        monitorConfig = JSONObject.parseObject(rootObj.toJSONString(), MonitorConfig.class);
    }


    /**
     * kafka配置
     *
     * @return
     */
    public static Properties getKafkaProducerConf() {
        Properties props = new Properties();
        props.setProperty(bootstrapServers, KafkaBaseInfoService.getInstance().reandomBrokerHost());
        props.put(keyDeserializer, kafkaconfig.getKeyDeserializer());
        props.put(valueDeserializer, kafkaconfig.getValueDeserializer());
        props.put(keySerializer, kafkaconfig.getKeySerializer());
        props.put(valueSerializer, kafkaconfig.getValueSerializer());
        return props;
    }

    /**
     * consumer配置
     *
     * @return
     */
    public static Properties getKafkaConsumerConf() {
        Properties props = new Properties();
        props.setProperty(bootstrapServers, KafkaBaseInfoService.getInstance().reandomBrokerHost());
        props.put(enableAutoCommit, kafkaconfig.getEnableAutoCommit());
        props.put(autoCommitIntervalMs, kafkaconfig.getAutoCommitIntervalMs());
        props.put(sessionTimeoutMs, kafkaconfig.getSessionTimeoutMs());
        props.put(keyDeserializer, kafkaconfig.getKeyDeserializer());
        props.put(valueDeserializer, kafkaconfig.getValueDeserializer());
        props.put(keySerializer, kafkaconfig.getKeySerializer());
        props.put(valueSerializer, kafkaconfig.getValueSerializer());
        props.put(groupId, kafkaconfig.getGroupId());
        return props;
    }

    /**
     * zookeeper配置
     *
     * @return
     */
    public static Properties getZkProper() {
        Properties props = new Properties();
        props.put(ZooConfig.HOST, zooConfig.getHost());
        props.put(ZooConfig.SESSION_TIMEOUT_MS, zooConfig.getSessionTimeoutMs());
        props.put(ZooConfig.CONNECTION_TIMEOUT_MS, zooConfig.getConnectionTimeoutMs());
        props.put(ZooConfig.RETRY_ONE_TIME, zooConfig.getRetryOneTime());
        return props;
    }


}
