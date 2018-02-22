package co.solinx.kafka.monitor.core.produce;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/22.
 */
public class ProduceRecord {

    private final String topic;
    private final int partition;
    private final String key;
    private final String value;

    public ProduceRecord(String topic, int partition, String key, String value) {
        this.topic = topic;
        this.partition = partition;
        this.key = key;
        this.value = value;
    }


    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "ProduceRecord{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
