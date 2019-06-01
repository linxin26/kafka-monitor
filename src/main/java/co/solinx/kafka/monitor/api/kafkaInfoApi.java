package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.ConfigService;
import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Broker;
import co.solinx.kafka.monitor.model.PageData;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@RestController
@RequestMapping("/data/kafkaInfoServlet")
public class kafkaInfoApi extends AbstractApi {

    KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();

    @RequestMapping
    public String kafkaInfo(String callback) {
        pageData = new PageData();
        List<Broker> brokersMap = service.getBrokers();
        List<Topic> topicList = service.getTopics();
        JSONObject zkConfigObj = ConfigService.rootObj;


        JSONObject extend = new JSONObject();

        extend.put("servers", KafkaBaseInfoService.getInstance().randomBroker().getHost());
        extend.put("partitionNum", topicList.stream().mapToInt((t) -> t.getPartitionMap().size()).sum());
        extend.put("clusterState", clusterState(topicList));
        extend.put("topicNum", topicList.size());
        extend.put("brokerNum", brokersMap.size());
        extend.put("zkConfig", zkConfigObj);
        pageData.setExtend(extend);
        return formatData(callback, pageData);
    }

    public boolean clusterState(List<Topic> topicList) {
        boolean result = true;
        for (Topic
                topic : topicList) {
            int preferred = (int) topic.getPreferred();
            if (preferred != 100) {
                result = false;
            }
        }
        return result;
    }

}
