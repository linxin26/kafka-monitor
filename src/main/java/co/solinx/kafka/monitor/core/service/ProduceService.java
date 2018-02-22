package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.core.produce.MonitorProducer;
import co.solinx.kafka.monitor.model.ZooConfig;
import com.alibaba.fastjson.JSONObject;
import co.solinx.kafka.monitor.common.Utils;
import co.solinx.kafka.monitor.core.produce.ProduceRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.*;
import org.apache.kafka.common.metrics.stats.Rate;
import org.apache.kafka.common.metrics.stats.Total;
import org.apache.kafka.common.utils.SystemTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控消息生产者
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/22.
 */
public class ProduceService {

    private final Logger logger = LoggerFactory.getLogger(ProduceService.class);
    ProduceMetrics produceMetrics;
    private String METRIC_GROUP_NAME = "produce-service";
    private ScheduledExecutorService produceExecutor;
    private ScheduledExecutorService partitionHandlerExecutor;
    /**
     * 消息生产者
     */
    private MonitorProducer producer;
    private AtomicInteger partitionNum;
    private String MONITOR_TOPIC;
    private String zkConnect;
    private ConcurrentMap<Integer, AtomicLong> currentPartition;


    public ProduceService() {

        Properties properties = ConfigService.getZkProper();
        zkConnect = properties.getProperty(ZooConfig.HOST);
        MONITOR_TOPIC = ConfigService.monitorConfig.getMonitorTopic();

        produceExecutor = Executors.newScheduledThreadPool(4, r -> new Thread(r, "produce-service"));
        partitionHandlerExecutor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "partition-change-handler"));
        partitionNum = new AtomicInteger(1);
        currentPartition = new ConcurrentHashMap<>();

        MetricConfig metricConfig = new MetricConfig().samples(60).timeWindow(100, TimeUnit.MILLISECONDS);
        List<MetricsReporter> reporters = new ArrayList<>();
        reporters.add(new JmxReporter("kmf.services"));
        Metrics metrics = new Metrics(metricConfig, reporters, new SystemTime());
        Map<String, String> tags = new HashMap<>();
        tags.put("name", "test");
        produceMetrics = new ProduceMetrics(metrics, tags);

        int existingPartitionCount = Utils.getPartitionNumByTopic(zkConnect, MONITOR_TOPIC);
        if (existingPartitionCount > 0) {
            partitionNum.set(existingPartitionCount);
        }

        initialProducer();

    }

    /**
     * 初始化生产者服务
     */
    private void initialProducer() {
        Properties props = ConfigService.getKafkaProducerConf();

        producer = new MonitorProducer(props);
    }

    /**
     * 初始化生产者Schedule，每个partition一个线程
     */
    private void initialProducerSchedule() {
        for (int partition = 0; partition < partitionNum.get(); partition++) {
            if (!currentPartition.containsKey(partition)) {
                produceMetrics.addPartitionSensor(partition);
                currentPartition.put(partition, new AtomicLong(0));
            }
            produceExecutor.scheduleWithFixedDelay(new ProduceRunnable(partition), 20000, 1000, TimeUnit.MILLISECONDS);
        }
    }


    public void start() {

        initialProducerSchedule();
        partitionHandlerExecutor.scheduleWithFixedDelay(new PartitionHandler(), 30000, 30000, TimeUnit.MILLISECONDS);
    }


    /**
     * producer度量
     */
    private class ProduceMetrics {

        private final Metrics metrics;
        final Sensor recordsProduce;
        final Sensor errorProduce;
        private final ConcurrentMap<Integer, Sensor> _recordsProducedPerPartition;
        private final ConcurrentMap<Integer, Sensor> _produceErrorPerPartition;
        private final Map<String, String> tags;

        public ProduceMetrics(Metrics metrics, final Map<String, String> tags) {
            this.metrics = metrics;
            this.tags = tags;


            _recordsProducedPerPartition = new ConcurrentHashMap<>();
            _produceErrorPerPartition = new ConcurrentHashMap<>();


            recordsProduce = metrics.sensor("records-produced");
            recordsProduce.add(new MetricName("records-produced-total", METRIC_GROUP_NAME, "The total number of records that are produced", tags), new Total());
            errorProduce = metrics.sensor("error-produce");
            errorProduce.add(new MetricName("error-produce-total", METRIC_GROUP_NAME, "", tags), new Total());

            metrics.addMetric(new MetricName("produce-availability-avg", METRIC_GROUP_NAME, "The average produce availability", tags),
                    (config, now) -> {
                        double availabilitySum = 0.0;
                        //可用性等于每个partition的可用性之和除以partition总数
                        //partition可用性等于成功发送率除以失败率
                        int num = partitionNum.get();

                        for (int partition = 0; partition < num; partition++) {
                            double recordsProduced = produceMetrics.metrics.metrics().get(new MetricName("records-produced-rate-partition-" + partition, METRIC_GROUP_NAME, tags)).value();
                            double produceError = produceMetrics.metrics.metrics().get(new MetricName("produce-error-rate-partition-" + partition, METRIC_GROUP_NAME, tags)).value();

                            if (Double.isNaN(produceError) || Double.isInfinite(produceError)) {
                                produceError = 0;
                            }
                            if (recordsProduced + produceError > 0) {
                                availabilitySum += recordsProduced / (recordsProduced + produceError);
                            }
                        }
                        return availabilitySum / num;
                        //return 0;
                    });


        }

        /**
         * 为每个partition添加Sensor
         *
         * @param partition
         */
        public void addPartitionSensor(int partition) {
            try {
                Sensor recordsProducedSensor = metrics.sensor("records-produced-partition-" + partition);
                recordsProducedSensor.add(new MetricName("records-produced-rate-partition-" + partition, METRIC_GROUP_NAME,
                        "The average number of records per second that are produced to this partition", tags), new Rate());
                _recordsProducedPerPartition.put(partition, recordsProducedSensor);

                Sensor errorsSensor = metrics.sensor("produce-error-partition-" + partition);
                errorsSensor.add(new MetricName("produce-error-rate-partition-" + partition, METRIC_GROUP_NAME,
                        "The average number of errors per second when producing to this partition", tags), new Rate());
                _produceErrorPerPartition.put(partition, errorsSensor);
            } catch (Exception e) {
                logger.error("addPartitionSensor exception {}", e);
            }
        }
    }

    /**
     * producer生产者线程
     */
    private class ProduceRunnable implements Runnable {

        int partition;


        public ProduceRunnable(int partition) {
            this.partition = partition;
        }

        @Override
        public void run() {
            try {

                long nextIndex = currentPartition.get(partition).get();

                //组装消息，time用于consumer判断消息的延迟,index用于判断消息的重复与丢失，index为当前消息的序号

                JSONObject messageObj = new JSONObject();
                messageObj.put("topic", MONITOR_TOPIC);
                messageObj.put("time", System.currentTimeMillis());
                messageObj.put("partition", partition);
                messageObj.put("index", nextIndex);
//                String message = String.format("topic:%s,partition:%d,time:%s", MONITOR_TOPIC, partition, System.currentTimeMillis());

                ProduceRecord produceRecord = new ProduceRecord(MONITOR_TOPIC, partition, null, messageObj.toJSONString());
                RecordMetadata metadata = producer.send(produceRecord);
                produceMetrics.recordsProduce.record();
                produceMetrics._recordsProducedPerPartition.get(partition).record();


                currentPartition.get(partition).getAndIncrement();
            } catch (Exception e) {
                produceMetrics.errorProduce.record();
                produceMetrics._produceErrorPerPartition.get(partition).record();
                logger.warn("failed to send message ", e);
            }
        }
    }

    /**
     * Partition变更处理，如有新增partition 需要关闭当前executor，后重新初始化executor
     */
    private class PartitionHandler implements Runnable {

        @Override
        public void run() {
            int currentPartitionNum = Utils.getPartitionNumByTopic(zkConnect, MONITOR_TOPIC);
            if (currentPartitionNum > partitionNum.get()) {
                produceExecutor.shutdown();

                try {
                    produceExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }

                producer.close();
                partitionNum.set(currentPartitionNum);

                initialProducer();
                produceExecutor = Executors.newScheduledThreadPool(4, r -> new Thread(r, "produce-service"));
                initialProducerSchedule();

            }
        }
    }

}
