package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.common.DateUtils;
import co.solinx.kafka.monitor.db.DBUtils;
import co.solinx.kafka.monitor.model.KafkaMonitorData;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@Path("/historyMetricsServlet")
public class HistoryMetricsApi extends AbstractApi {

    private DBUtils<KafkaMonitorData> dbUtils = new DBUtils<>(KafkaMonitorData.class);
    private Logger logger = LoggerFactory.getLogger(HistoryMetricsApi.class);

    @GET
    @Path("{type}")
    public String historyData(@PathParam("type") String type, @QueryParam("startTime") String startTime,
                              @QueryParam("endTime") String endTime, @QueryParam("callback") String callback) {
        String where = " where currentTime >= '" + startTime + "' and currentTime <='" + endTime + "'";

        JSONObject resultObject;

        try {

            List<KafkaMonitorData> dataList = dbUtils.query("select * from kafkaMonitorData " + where);

            resultObject = getMetricsByType(dataList, type);
            pageData.setData(resultObject);
            return formatData(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getMetricsByType(List<KafkaMonitorData> dataList, String type) {

        List<Double> consumerAvgList = new ArrayList<>();
        List<Double> producerAvgList = new ArrayList<>();
        List<Double> consumerErrorList = new ArrayList<>();
        List<Double> producerErrorList = new ArrayList<>();
        List<Double> delayList = new ArrayList<>();
        List<Double> delayedRateList = new ArrayList<>();
        List<Double> delayMsAvgList = new ArrayList<>();
        List<Double> delayMsMaxList = new ArrayList<>();
        List<Double> duplicatedList = new ArrayList<>();
        List<Double> duplicatedRateList = new ArrayList<>();
        List<Double> lostRateList = new ArrayList<>();
        List<Double> lostTotalList = new ArrayList<>();
        List<Double> producerTotalList = new ArrayList<>();
        List<Double> consumerTotalList = new ArrayList<>();


        List<String> timeList = new ArrayList<>();
        for (KafkaMonitorData model :
                dataList) {
            consumerAvgList.add(model.getConsumeAvailabilityAvg());
            producerAvgList.add(model.getProduceAvailabilityAvg());
            consumerErrorList.add(model.getConsumerError());
            consumerTotalList.add(model.getConsumerTotal());
            delayList.add(model.getDelay());
            delayedRateList.add(model.getDelayedRate());
            delayMsAvgList.add(model.getDelayMsAvg());
            delayMsMaxList.add(model.getDelayMsMax());
            duplicatedList.add(model.getDuplicated());
            duplicatedRateList.add(model.getDuplicatedRate());
            lostRateList.add(model.getLostRate());
            lostTotalList.add(model.getLostTotal());
            producerErrorList.add(model.getProducerError());
            producerTotalList.add(model.getProducerTotal());

            timeList.add(DateUtils.getTimeStr(model.getCurrentTime(), DateUtils.HYPHEN_DISPLAY_DATE));
        }
        JSONObject resultObj = new JSONObject();

        switch (type) {
            case "total":
                resultObj.put("producerTotal", producerTotalList);
                resultObj.put("consumerTotal", consumerTotalList);
                break;
            case "avg":
                resultObj.put("producerAvg", producerAvgList);
                resultObj.put("consumerAvg", consumerAvgList);
                break;
            case "delayed":
                resultObj.put("delayed", delayList);
                resultObj.put("duplicated", duplicatedList);
                break;
            case "error":
                resultObj.put("lostTotal", lostTotalList);
                resultObj.put("consumerError", consumerErrorList);
                resultObj.put("producerError", producerErrorList);
                break;
            case "rate":
                resultObj.put("duplicatedRate", duplicatedRateList);
                resultObj.put("lostRate", lostRateList);
                resultObj.put("delayedRate", delayedRateList);
                break;
            case "delay":
                resultObj.put("delayMsAvg", delayMsAvgList);
                resultObj.put("delayMsMax", delayMsMaxList);
                break;
            default:
                break;
        }

        resultObj.put("time", timeList);
        return resultObj;
    }
}
