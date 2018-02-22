package co.solinx.kafka.monitor.core.produce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * MonitorProducer
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/22.
 */
public class MonitorProducer {
    private KafkaProducer<String, String> producer;


    public MonitorProducer(Properties properties) {
        producer = new KafkaProducer<>(properties);
    }

    public RecordMetadata send(ProduceRecord record) throws Exception {
        ProducerRecord producerRecord = new ProducerRecord(record.getTopic(), record.getPartition(), record.getKey(), record.getValue());

        Future<RecordMetadata> metadataFuture = producer.send(producerRecord);
        return metadataFuture.get();
    }


    public void close() {
        producer.close();
    }

}
