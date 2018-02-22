package co.solinx.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/12.
 */
public class KafkaConfigTest {

    static Logger logger = LoggerFactory.getLogger(KafkaConfigTest.class);

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "10.10.1.104:9093");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //“所有”设置将导致记录的完整提交阻塞，最慢的，但最持久的设置。
        //properties.put("acks", "all");
        //如果请求失败，生产者也会自动重试，即使设置成０ the producer can automatically retry.
        properties.put("retries", 1);
        //The producer maintains buffers of unsent records for each partition.
        properties.put("batch.size", 16384);
        //默认立即发送，这里这是延时毫秒数
        properties.put("linger.ms", 1);
        //生产者缓冲大小，当缓冲区耗尽后，额外的发送调用将被阻塞。时间超过max.block.ms将抛出TimeoutException
        properties.put("buffer.memory", 34432);

        System.out.println("start...");
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);

        logger.debug("config {}", properties);





        while(true) {

            for (int i = 0; i < 1000; i++) {
                System.out.println("start......");
                producer.send(new ProducerRecord<String, String>("foo", Integer.toString(i), Integer.toString(i)));
                System.out.println("send " + i);

            }

            //producer.close();
            producer.flush();

        logger.debug("---------metrics{}", producer.metrics());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}
