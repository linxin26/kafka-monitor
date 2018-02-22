package co.solinx.kafka.monitor.core.consumer;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/23.
 */
public class MonitorConsumerRecord {

    private final String topic;
    private final int partition;
    private final long offset;
    private final String key;
    private final String value;


    public MonitorConsumerRecord(String topic, int partition, long offset, String key, String value) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.key = key;
        this.value = value;
    }


    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
