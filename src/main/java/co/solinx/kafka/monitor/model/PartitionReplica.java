package co.solinx.kafka.monitor.model;

import lombok.Data;

/**
 * 分区与副本
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/16.
 */
@Data
public class PartitionReplica {

    /**
     * leader副本Id
     */
    private Integer id;
    private boolean inService;
    private boolean leader;

    public PartitionReplica() {
    }

    public PartitionReplica(Integer id, boolean inService, boolean leader) {
        this.id = id;
        this.inService = inService;
        this.leader = leader;
    }


    @Override
    public String toString() {
        return "PartitionReplica{" +
                "id=" + id +
                ", inService=" + inService +
                ", leader=" + leader +
                '}';
    }
}
