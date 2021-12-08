package co.solinx.kafka.monitor.core.service;

import co.solinx.kafka.monitor.model.KafkaMonitorData;
import com.alibaba.fastjson.JSONObject;
import co.solinx.kafka.monitor.common.DateUtils;
import co.solinx.kafka.monitor.persist.MetricsDataPersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/22.
 */
public class MetricsReportService {

    private Logger logger = LoggerFactory.getLogger(MetricsReportService.class);
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static Map<String, ArrayList> totalMap = new HashMap();
    private static Map<String, ArrayList> avgMap = new HashMap();
    private static Map<String, ArrayList> delayedMap = new HashMap();
    private static Map<String, ArrayList> errorMap = new HashMap();
    private static Map<String, ArrayList> delayMap = new HashMap();
    private static Map<String, ArrayList> rateMap = new HashMap();
    private static List<String> timeList = new ArrayList<>();
    private static MetricsReportService metricsReportService = null;

    private MetricsReportService() {

        totalMap.put("producerData", new ArrayList());
        totalMap.put("consumerData", new ArrayList());

        avgMap.put("producerAvg", new ArrayList());
        avgMap.put("consumerAvg", new ArrayList());


        delayedMap.put("delayed", new ArrayList());
        delayedMap.put("duplicated", new ArrayList());

        errorMap.put("producerError", new ArrayList());
        errorMap.put("consumerError", new ArrayList());
        errorMap.put("lostTotal", new ArrayList());

        delayMap.put("delayMsAvg", new ArrayList());
        delayMap.put("delayMsMax", new ArrayList());

        rateMap.put("duplicatedRate", new ArrayList());
        rateMap.put("lostRate", new ArrayList());
        rateMap.put("delayedRate", new ArrayList());
    }

    public static MetricsReportService getMetricsService() {
        if (metricsReportService == null) {
            metricsReportService = new MetricsReportService();
        }
        return metricsReportService;
    }


    public void start() {
        executor.scheduleAtFixedRate((Runnable) () -> reportMetrics(), 3000, 3000, TimeUnit.MILLISECONDS);
    }

    private void reportMetrics() {
        try {
            removeRecord();

            List<String> metricNames = new ArrayList<>();
            metricNames.add("kmf.services:type=produce-service,name=*:produce-availability-avg");
            metricNames.add("kmf.services:type=consumer-service,name=*:consume-availability-avg");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-delay-ms-avg");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-delay-ms-max");
            metricNames.add("kmf.services:type=produce-service,name=*:error-produce-total");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-lost-total");
            metricNames.add("kmf.services:type=consumer-service,name=*:consume-error-total");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-duplicated-rate");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-delayed-rate");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-lost-rate");
            metricNames.add("kmf.services:type=produce-service,name=*:records-produced-total");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-consumed-total");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-delayed-total");
            metricNames.add("kmf.services:type=consumer-service,name=*:records-duplicated-total");

            getMetricsByType(metricNames);


        } catch (Exception e) {
            logger.error("reportMetrics error {} ", e);
        }

    }


    private JSONObject getMetricsByType(List<String> metricNames) {

        JSONObject resultObj = new JSONObject();
        KafkaMonitorData model = new KafkaMonitorData();
        for (String metricName : metricNames) {
            String mbeanExpr = metricName.substring(0, metricName.lastIndexOf(":"));
            String attributeExpr = metricName.substring(metricName.lastIndexOf(":") + 1);

            List<MbeanAttributeValue> attributeValues = getMBeanAttributeValues(mbeanExpr, attributeExpr);

            for (MbeanAttributeValue attributeValue : attributeValues) {
                switch (attributeValue._attribute) {
                    case "produce-availability-avg":
                        ArrayList producerAvg = avgMap.get("producerAvg");
                        producerAvg.add(attributeValue._value);
                        model.setProduceAvailabilityAvg(attributeValue._value);
                        break;
                    case "consume-availability-avg":
                        ArrayList consumerAvg = avgMap.get("consumerAvg");
                        consumerAvg.add(attributeValue._value);
                        model.setConsumeAvailabilityAvg(attributeValue._value);
                        break;
                    case "records-produced-total":
                        ArrayList producerDataList = totalMap.get("producerData");
                        producerDataList.add(attributeValue._value);
                        model.setProducerTotal(attributeValue._value);
                        break;
                    case "records-consumed-total":
                        ArrayList consumerDataList = totalMap.get("consumerData");
                        consumerDataList.add(attributeValue._value);
                        model.setConsumerTotal(attributeValue._value);
                        break;
                    case "records-delayed-total":
                        ArrayList delayedList = delayedMap.get("delayed");
                        delayedList.add(attributeValue._value);
                        model.setDelay(attributeValue._value);
                        break;
                    case "records-duplicated-total":
                        ArrayList duplicatedList = delayedMap.get("duplicated");
                        duplicatedList.add(attributeValue._value);
                        model.setDuplicated(attributeValue._value);
                        break;
                    case "error-produce-total":
                        ArrayList producerErrorList = errorMap.get("producerError");
                        producerErrorList.add(attributeValue._value);
                        model.setProducerError(attributeValue._value);
                        break;
                    case "consume-error-total":
                        ArrayList consumerErrorList = errorMap.get("consumerError");
                        consumerErrorList.add(attributeValue._value);
                        model.setConsumerError(attributeValue._value);
                        break;
                    case "records-lost-total":
                        ArrayList lostList = errorMap.get("lostTotal");
                        lostList.add(attributeValue._value);
                        model.setLostTotal(attributeValue._value);
                        break;
                    case "records-delay-ms-avg":
                        ArrayList delayMsAvgList = delayMap.get("delayMsAvg");
                        delayMsAvgList.add(attributeValue._value);
                        model.setDelayMsAvg(attributeValue._value);
                        break;
                    case "records-delay-ms-max":
                        ArrayList delayMsMaxList = delayMap.get("delayMsMax");
                        delayMsMaxList.add(attributeValue._value);
                        model.setDelayMsMax(attributeValue._value);
                        break;
                    case "records-duplicated-rate":
                        ArrayList duplicatedRate = rateMap.get("duplicatedRate");
                        duplicatedRate.add(attributeValue._value);
                        model.setDuplicatedRate(attributeValue._value);
                        break;
                    case "records-lost-rate":
                        ArrayList lostRate = rateMap.get("lostRate");
                        lostRate.add(attributeValue._value);
                        model.setLostRate(attributeValue._value);
                        break;
                    case "records-delayed-rate":
                        ArrayList delayedRate = rateMap.get("delayedRate");
                        delayedRate.add(attributeValue._value);
                        model.setDelayedRate(attributeValue._value);
                        break;
                    default:
                        break;
                }
            }
        }
        String currentTime = DateUtils.getCurrTimeStr(DateUtils.HYPHEN_DISPLAY_DATE);
        timeList.add(currentTime);
        model.setCurrentTime(DateUtils.convertFromStringToDate(currentTime, "yyyy-MM-dd HH:mm:ss"));

     //   persist.toDB(model);
        return resultObj;
    }


    public JSONObject getAvgMetrics() {
        JSONObject resultObj = new JSONObject();
        resultObj.put("producerAvg", avgMap.get("producerAvg"));
        resultObj.put("consumerAvg", avgMap.get("consumerAvg"));
        resultObj.put("time", timeList);
        return resultObj;
    }

    public JSONObject getDelayMetrics() {
        JSONObject resultObj = new JSONObject();

        resultObj.put("delayMsAvg", delayMap.get("delayMsAvg"));
        resultObj.put("delayMsMax", delayMap.get("delayMsMax"));
        resultObj.put("time", timeList);
        return resultObj;
    }

    public JSONObject getErrorMetrics() {

        JSONObject resultObj = new JSONObject();

        resultObj.put("lostTotal", errorMap.get("lostTotal"));
        resultObj.put("consumerError", errorMap.get("consumerError"));
        resultObj.put("producerError", errorMap.get("producerError"));
        resultObj.put("time", timeList);
        return resultObj;
    }

    public JSONObject getRateMetrics() {

        JSONObject resultObj = new JSONObject();

        resultObj.put("duplicatedRate", rateMap.get("duplicatedRate"));
        resultObj.put("lostRate", rateMap.get("lostRate"));
        resultObj.put("delayedRate", rateMap.get("delayedRate"));
        resultObj.put("time", timeList);
        return resultObj;
    }

    public JSONObject getDelayedMetrics() {
        JSONObject resultObj = new JSONObject();
        resultObj.put("delayed", delayedMap.get("delayed"));
        resultObj.put("duplicated", delayedMap.get("duplicated"));
        resultObj.put("time", timeList);
        return resultObj;
    }


    public JSONObject getTotalMetrics() {
        JSONObject resultObj = new JSONObject();
        resultObj.put("producerTotal", totalMap.get("producerData"));
        resultObj.put("consumerTotal", totalMap.get("consumerData"));
        resultObj.put("time", timeList);
        return resultObj;
    }

    public void removeRecord() {

        ArrayList totalArray = totalMap.get("producerData");
        ArrayList consumerData = totalMap.get("consumerData");

        ArrayList avgArray = avgMap.get("producerAvg");
        ArrayList consumerAvg = avgMap.get("consumerAvg");

        ArrayList delayed = delayedMap.get("delayed");
        ArrayList duplicated = delayedMap.get("duplicated");

        ArrayList lostTotal = errorMap.get("lostTotal");
        ArrayList consumerError = errorMap.get("consumerError");
        ArrayList producerError = errorMap.get("producerError");

        ArrayList delayMsAvg = delayMap.get("delayMsAvg");
        ArrayList delayMsMax = delayMap.get("delayMsMax");

        ArrayList duplicatedRate = rateMap.get("duplicatedRate");
        ArrayList lostRate = rateMap.get("lostRate");
        ArrayList delayedRate = rateMap.get("delayedRate");

        while (timeList.size() > 20) {
            totalArray.remove(0);
            consumerData.remove(0);
            avgArray.remove(0);
            consumerAvg.remove(0);
            delayed.remove(0);
            duplicated.remove(0);
            lostTotal.remove(0);
            consumerError.remove(0);
            producerError.remove(0);
            delayMsAvg.remove(0);
            delayMsMax.remove(0);
            duplicatedRate.remove(0);
            lostRate.remove(0);
            delayedRate.remove(0);
            timeList.remove(0);
        }
    }

    private List<MbeanAttributeValue> getMBeanAttributeValues(String mbeanExpr, String attributeExpr) {
        List<MbeanAttributeValue> values = new ArrayList<>();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            Set<ObjectName> mbeanNames = server.queryNames(new ObjectName(mbeanExpr), null);
            for (ObjectName mbeanName : mbeanNames) {
                MBeanInfo mBeanInfo = server.getMBeanInfo(mbeanName);
                MBeanAttributeInfo[] attributeInfos = mBeanInfo.getAttributes();
                for (MBeanAttributeInfo attributeInfo : attributeInfos) {
                    if (attributeInfo.getName().equals(attributeExpr) || attributeExpr.length() == 0 || attributeExpr.equals("*")) {
                        double value = (Double) server.getAttribute(mbeanName, attributeInfo.getName());
                        values.add(new MbeanAttributeValue(mbeanName.getCanonicalName(), attributeInfo.getName(), value));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return values;
    }

    static class MbeanAttributeValue {
        private final String _mbean;
        private final String _attribute;
        private final double _value;

        public MbeanAttributeValue(String mbean, String attribute, double value) {
            _mbean = mbean;
            _attribute = attribute;
            _value = value;
        }

        public String get_attribute() {
            return _attribute;
        }

        public double get_value() {
            return _value;
        }

        public String get_mbean() {
            return _mbean;
        }

        @Override
        public String toString() {
            return _mbean + ":" + _attribute + "=" + _value;
        }
    }

}
