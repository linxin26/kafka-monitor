package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.common.KafkaUtils;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/13.
 */
public class PartitionService {

    private ZkUtils zkUtils = KafkaUtils.getZkUtils();

    public void addPartition(String topic, int partitions, String replica) {
        AdminUtils.addPartitions(zkUtils, topic, partitions, replica, true, RackAwareMode.Enforced$.MODULE$);
    }

    /**
     * 添加分区
     *
     * @param topic      topic名称
     * @param partitions 分区数
     */
    public void addPartition(String topic, int partitions) {
        this.addPartition(topic, partitions, "");
    }

}
