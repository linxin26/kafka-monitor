package co.solinx.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/12.
 */
public class KafkaConsumerTest {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaConsumerTest.class);


    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "10.10.1.104:9093");
        props.put("group.id", "test2");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        consumer.subscribe(Arrays.asList("foo"));

        logger.debug("config {}", props);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(2);
            for (ConsumerRecord record : records) {
                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
            }
            //logger.debug("{}",records);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
