package co.solinx.kafka.monitor.model;

import lombok.Data;

/**
 * Consumer Model
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/20.
 */
@Data
public class Consumer {

    /**
     * consumerId
     */
    private String memberId;
    /**
     * consumerGroup
     */
    private String group;
    /**
     * topic
     */
    private String topic;
    /**
     * 当前partition
     */
    private int partition;
    /**
     * 当前offset
     */
    private long currentOffset;
    /**
     * 最后一条offset
     */
    private long endOffset;
    private long lag;
    private String owner;
    private String clientId;
    private String host;


    @Override
    public String toString() {
        return "Consumer{" +
                "memberId='" + memberId + '\'' +
                ", group='" + group + '\'' +
                ", topic='" + topic + '\'' +
                ", partition=" + partition +
                ", currentOffset=" + currentOffset +
                ", endOffset=" + endOffset +
                ", lag=" + lag +
                ", owner='" + owner + '\'' +
                ", clientId='" + clientId + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
