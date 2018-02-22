package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.common.KafkaUtils;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.admin.RackAwareMode$;
import kafka.utils.ZkUtils;

import java.util.Properties;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/13.
 */
public class TopicService {

    private ZkUtils zkUtils = KafkaUtils.getZkUtils();


    /**
     * 添加topic
     *
     * @param topic             topic名称
     * @param partitions        分区数
     * @param replicationFactor 副本因子
     */
    public void createTopic(String topic, int partitions, int replicationFactor) {

        AdminUtils.createTopic(zkUtils, topic, partitions, replicationFactor, new Properties(), RackAwareMode.Enforced$.MODULE$);
    }

    /**
     * 删除topic
     *
     * @param topic topic名称
     */
    public void deleteTopic(String topic) {
        AdminUtils.deleteTopic(zkUtils, topic);

    }


}
