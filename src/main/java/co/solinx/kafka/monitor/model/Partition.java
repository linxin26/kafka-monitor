package co.solinx.kafka.monitor.model;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * partition
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/14.
 */
@Data
public class Partition {

    /**
     * partitionId
     */
    private int id;
    /**
     * 分区副本
     */
    private Map<Integer, PartitionReplica> replicas = new HashMap<>();

    private int[] replicasArray;
    /**
     * leader节点
     */
    private Integer leader = -1;
    /**
     * 首选leaderId
     */
    private Integer preferredLeaderId;
    /**
     * lastOffset  该partition的最后一条消息偏移量
     */
    private long size = -1;
    /**
     * firstOffset 该partition的第一条消息偏移量
     */
    private long firstOffset = -1;

    private int[] isr;

    public Partition() {
    }

    public Partition(int id) {
        this.id = id;
    }

    public Collection<PartitionReplica> getReplicas() {
        return replicas.values();
    }


    /**
     * 添加副本
     *
     * @param replica
     */
    public void addReplica(PartitionReplica replica) {
        replicas.put(replica.getId(), replica);
        if (replica.isLeader()) {
            leader = replica.getId();
        }
    }

    /**
     * 副本leader
     *
     * @return
     */
    public PartitionReplica getLeader() {
        return replicas.get(leader);
    }

    public Integer getLeaderId() {
        return leader;
    }


    /**
     * ISR集合
     *
     * @return
     */
    public List<PartitionReplica> getInSyncReplicas() {
        return inSyncReplicaStream()
                .sorted(Comparator.comparingInt(PartitionReplica::getId))
                .collect(Collectors.toList());
    }

    private Stream<PartitionReplica> unSyncReplicaStream() {
        return replicas.values().stream()
                .filter(p -> !p.isInService());
    }

    private Stream<PartitionReplica> inSyncReplicaStream() {
        return replicas.values().stream()
                .filter(PartitionReplica::isInService);
    }

    //todo
    public boolean isUnderReplicated() {
//        long isrCount=isr.length;
        int replicasCount=replicasArray.length;
        long isrCount=inSyncReplicaStream().count();
//        int replicasCount=replicas.size();
        return  isrCount< replicasCount;
    }

    public boolean isLeaderPreferred() {

        return leader.equals(preferredLeaderId);
    }

    @Override
    public String toString() {
        return "TopicPartition{" +
                "id=" + id +
                ", replicas=" + replicas +
                ", leader=" + leader +
                ", preferredLeaderId=" + preferredLeaderId +
                ", size=" + size +
                ", firstOffset=" + firstOffset +
                '}';
    }
}
