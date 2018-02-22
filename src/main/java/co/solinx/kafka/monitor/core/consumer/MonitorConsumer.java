package co.solinx.kafka.monitor.core.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/23.
 */
public class MonitorConsumer {
    private KafkaConsumer<String, String> consumer;
    private Iterator<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> recordIterator;


    public MonitorConsumer(String topic, Properties properties) {
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
    }

    public MonitorConsumerRecord receive() {
        if (recordIterator == null || !recordIterator.hasNext())
            recordIterator = consumer.poll(Long.MAX_VALUE).iterator();

        ConsumerRecord<String, String> record = recordIterator.next();
        return new MonitorConsumerRecord(record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }

    public void close() {
        consumer.close();
    }
}
