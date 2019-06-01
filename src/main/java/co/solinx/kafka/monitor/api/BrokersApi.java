package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Broker;
import co.solinx.kafka.monitor.model.PageData;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * broker信息
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/26.
 */

@RestController
@RequestMapping("/data/brokerServlet")
public class BrokersApi extends AbstractApi {

    private Logger logger = LoggerFactory.getLogger(BrokersApi.class);
    private KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();

    @RequestMapping
    public String brokers(String callback) {
        pageData = new PageData();
        List<Broker> brokerList = service.getBrokers();

        pageData.setData(brokerList);
        return formatData(callback, pageData);
    }

    @RequestMapping(value = "/{id}",method= RequestMethod.GET)
    public String getBrokerById(@PathVariable int id,String callback) {
        pageData = new PageData();
        Broker broker = service.getBrokerById(id);
        List<Topic> topicList = service.getTopics();
        int partitionCount = 0;
        for (Topic topic :
                topicList) {
            partitionCount += topic.getLeaderPartitions(id).size();
        }

        pageData.setData(broker);

        JSONObject extend = new JSONObject();
        extend.put("partitionCount", partitionCount);
        extend.put("topicCount", topicList.size());
        pageData.setExtend(extend);
        return formatData(callback, pageData);
    }

    @RequestMapping("/summary")
    public String getSummary(String callback) {
        pageData = new PageData();
        List<Broker> brokerList = service.getBrokers();
        JSONObject result = new JSONObject();
        result.put("brokerTotal", brokerList.size());
        result.put("brokerAbleTotal", brokerList.size());

        pageData.setData(result);
        return formatData(callback, pageData);
    }

}
