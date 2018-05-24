package co.solinx.kafka.monitor.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import co.solinx.kafka.monitor.core.consumer.MonitorConsumer;
import co.solinx.kafka.monitor.core.consumer.MonitorConsumerRecord;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.*;
import org.apache.kafka.common.metrics.stats.*;
import org.apache.kafka.common.utils.SystemTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 监控程序consumer写入服务
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/23.
 */
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    private static final String METRIC_GROUP_NAME = "consumer-service";

    private final String name = "monitor";
    private final Thread thread;
    private final ConsumerMetrics sensor;
    private MonitorConsumer consumer;
    private String MONITOR_TOPIC;
    private int delayedTime = 20_000;


    public ConsumerService() {

        thread = new Thread(() -> {
            consumer();
        }, name + "consumer-service");

        Properties props = ConfigService.getKafkaConsumerConf();
        MONITOR_TOPIC = ConfigService.monitorConfig.getMonitorTopic();

        consumer = new MonitorConsumer(MONITOR_TOPIC, props);

        MetricConfig metricConfig = new MetricConfig().samples(60).timeWindow(1000, TimeUnit.MILLISECONDS);
        List<MetricsReporter> reporterList = new ArrayList<>();
        reporterList.add(new JmxReporter("kmf.services"));
        Metrics metrics = new Metrics(metricConfig, reporterList, new SystemTime());
        Map<String, String> tags = new HashMap<>();
        tags.put("name", "monitor");
        sensor = new ConsumerMetrics(metrics, tags);
    }

    public void start() {
        thread.start();
    }

    //逻辑 producer生产消息时存入序号、时间
    //使用消息生产的时间与consumer消费的时间差作为延迟时间，延迟时间超过某个阀值时该消息延迟的
    //使用index判断消息的重复与失败，index为producer生产的序列，
    //consumer某个partition首次接收到消息时消息index=0将该partition的nextIndex设置为1，
    //consumer某个partition第二次收到消息时当前nextIndex为1，消息的index为1，正常情况下nextIndex==index，nextIndex=index+1
    //往后每次收到的消息消息正常时index与nextIndex都是相等的
    // index<nextIndex时消息接收重复了
    // index>nextIndex时，消息有丢失，丢失了index-nextIndex条消息，nextIndex=index+1
    public void consumer() {

        Map<Integer, Long> nextIndexs = new HashMap<>();
        while (true) {

            MonitorConsumerRecord record;
            try {
                record = consumer.receive();
                JSONObject messageObj = JSON.parseObject(record.getValue());
                long msgTime = messageObj.getLong("time");
                int msgPartition = record.getPartition();
                long index = messageObj.getLong("index");
                String topic = messageObj.getString("topic");
                long curTime = System.currentTimeMillis();
                //延迟时间
                long delayTime = curTime - msgTime;
                sensor.recordsDelay.record(delayTime);
                if (delayTime > delayedTime) {
                    sensor.recordsDelayed.record();
                }
                sensor.recordsConsume.record();

                if (!nextIndexs.containsKey(msgPartition)) {
                    nextIndexs.put(msgPartition, 1l);
                    continue;
                }

                long nextIndex = nextIndexs.get(msgPartition);
                if (index == nextIndex) {
                    nextIndexs.put(msgPartition, index + 1);
                } else if (index < nextIndex) {
                    sensor.recordsDuplicated.record();
                } else if (index > nextIndex) {
                    sensor.recordsLost.record(index - nextIndex);
                    nextIndexs.put(msgPartition, index + 1);
                }
            } catch (Exception e) {
                sensor.consumerError.record();
                logger.warn("{}", e);

                continue;
            }
        }

    }

    private class ConsumerMetrics {
        public final Metrics metrics;
        private final Sensor bytesConsume;
        private final Sensor consumerError;
        private final Sensor recordsConsume;
        private final Sensor recordsDuplicated;
        private final Sensor recordsLost;
        private final Sensor recordsDelay;
        private final Sensor recordsDelayed;


        public ConsumerMetrics(Metrics metrics, final Map<String, String> tags) {

            this.metrics = metrics;

            consumerError = metrics.sensor("consume-error");
            consumerError.add(new MetricName("consume-error-rate", METRIC_GROUP_NAME, "The average number of errors per second", tags), new Rate());
            consumerError.add(new MetricName("consume-error-total", METRIC_GROUP_NAME, "The total number of errors", tags), new Total());

            recordsConsume = metrics.sensor("records-consumed");
            recordsConsume.add(new MetricName("records-consumed-rate", METRIC_GROUP_NAME, "The average number of records per second that are consumed", tags), new Rate());
            recordsConsume.add(new MetricName("records-consumed-total", METRIC_GROUP_NAME, "The total number of records that are consumed", tags), new Total());

            bytesConsume = metrics.sensor("bytes-consume");
            recordsDuplicated = metrics.sensor("records-duplicated");
            recordsDuplicated.add(new MetricName("records-duplicated-rate", METRIC_GROUP_NAME, "The average number of records per second that are duplicated", tags), new Rate());
            recordsDuplicated.add(new MetricName("records-duplicated-total", METRIC_GROUP_NAME, "The total number of records that are duplicated", tags), new Total());

            recordsDelay = metrics.sensor("records-delay");
            recordsDelay.add(new MetricName("records-delay-ms-avg", METRIC_GROUP_NAME, "The average latency of records from producer to consumer", tags), new SampledStat(0) {
                @Override
                protected void update(Sample sample, MetricConfig config, double value, long timeMs) {
                    sample.value += value;
                }

                @Override
                public double combine(List<Sample> samples, MetricConfig config, long now) {
                    double total = 0.0;
                    double count = 0;
                    for (int i = 0; i < samples.size(); i++) {
                        Sample s = samples.get(i);
                        total += s.value;
                        count += s.eventCount;
                    }
                    BigDecimal bTotal = new BigDecimal(Double.toString(total));
                    BigDecimal bCount = new BigDecimal(Double.toString(count));

                    return count == 0 ? 0 : bTotal.divide(bCount, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            });
            recordsDelay.add(new MetricName("records-delay-ms-max", METRIC_GROUP_NAME, "The maximum latency of records from producer to consumer", tags), new Max());

            recordsLost = metrics.sensor("records-lost");
            recordsLost.add(new MetricName("records-lost-rate", METRIC_GROUP_NAME, "The average number of records per second that are lost", tags), new Rate());
            recordsLost.add(new MetricName("records-lost-total", METRIC_GROUP_NAME, "The total number of records that are lost", tags), new Total());


            recordsDelayed = metrics.sensor("records-delayed");
            recordsDelayed.add(new MetricName("records-delayed-rate", METRIC_GROUP_NAME, "The average number of records per second that are either lost or arrive after maximum allowed latency under SLA", tags), new Rate());
            recordsDelayed.add(new MetricName("records-delayed-total", METRIC_GROUP_NAME, "The total number of records that are either lost or arrive after maximum allowed latency under SLA", tags), new Total());


            metrics.addMetric(new MetricName("consume-availability-avg", METRIC_GROUP_NAME, "The average consume availability", tags),
                    (config, now) -> {
                        double recordsConsumedRate = sensor.metrics.metrics().get(new MetricName("records-consumed-rate", METRIC_GROUP_NAME, tags)).value();
                        double recordsLostRate = sensor.metrics.metrics().get(new MetricName("records-lost-rate", METRIC_GROUP_NAME, tags)).value();
                        double recordsDelayedRate = sensor.metrics.metrics().get(new MetricName("records-delayed-rate", METRIC_GROUP_NAME, tags)).value();

                        if (new Double(recordsLostRate).isNaN()) {
                            recordsLostRate = 0;
                        }
                        if (new Double(recordsDelayedRate).isNaN()) {
                            recordsDelayedRate = 0;
                        }

                        double consumeAvailability = recordsConsumedRate + recordsLostRate > 0 ?
                                (recordsConsumedRate - recordsDelayedRate) / (recordsConsumedRate + recordsLostRate) : 0;
                          BigDecimal bg = new BigDecimal(consumeAvailability);
                        return bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                    });

        }
    }

}
