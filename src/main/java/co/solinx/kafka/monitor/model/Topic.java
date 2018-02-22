package co.solinx.kafka.monitor.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/13.
 */
@Data
public class Topic {


    private String brokerId;
    /**
     * 名称
     */
    private String name;
    /**
     * 配置
     */
    private Map<String, Object> config;
    /**
     * topic分区
     */
    private Map<Integer, Partition> partitionMap = new HashMap<>();

    private int partitionSum;

    /**
     * 副本leaderId/分区总数
     */
    private double preferred;

    /**
     *
     */
    private double underReplicated;

    private int version;
    private JSONObject partitions;

    public Topic(String name) {
        this.name = name;
    }

    public Topic() {

    }


    //public Collection<TopicPartition> getPartitions(){
//        return this.partitionMap.values();
//    }

    /**
     * 取分区信息
     *
     * @param partitionID
     * @return
     */
    public Partition getPartition(int partitionID) {
        return this.partitionMap.get(partitionID);
    }

    public Collection<Partition> getLeaderPartitions(int brokerID) {
        return partitionMap.values().
                stream().
                filter(temp -> temp.getLeader() != null && temp.getLeader().getId() == brokerID).collect(Collectors.toList());
    }

    /**
     * topic消息总数
     * topic的partition所有消息总数
     *
     * @return
     */
    public long getSize() {
        return partitionMap.values()
                .stream().map(p -> p.getSize())
                .filter((s)-> s!=-1)
                .reduce(0L, Long::sum);
    }

    /**
     * 可用消息总数
     *
     * @return
     */
    public long getAvailableSize() {
        return partitionMap.values()
                .stream().map(p -> p.getSize() - p.getFirstOffset())
                .reduce(0L, Long::sum);
    }

    public double getPreferredReplicaPercent() {
        long preferredLeaderCount = partitionMap.values().stream()
                .filter(Partition::isLeaderPreferred)
                .count();
        return ((double) preferredLeaderCount) / ((double) partitionMap.size());
    }

    public Collection<Partition> getUnderReplicatedPartitions() {
        return partitionMap.values().stream()
                .filter(Partition::isUnderReplicated)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", config='" + config + '\'' +
                ", partitionMap=" + partitionMap +
                '}';
    }
}
