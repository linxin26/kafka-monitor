package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@RestController
@RequestMapping("/data/brokerTopicServlet")
public class BrokerTopicServletApi extends AbstractApi {

    private static KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();
    private Logger logger = LoggerFactory.getLogger(BrokerTopicServletApi.class);

    @RequestMapping("/{brokerID}")
    public String topic(@PathVariable int brokerID, String callback) {
        List<Topic> topicList = service.getTopics();
        JSONArray array = new JSONArray();
        for (Topic topic :
                topicList) {
            Collection<Partition> topicPar = topic.getLeaderPartitions(brokerID);
            int partitionCount = topicPar.size();
            JSONObject topicObj = new JSONObject();
            topicObj.put("name", topic.getName());
            topicObj.put("partitionCount", topic.getPartitionMap().size());
            topicObj.put("brokerPartitionCount", partitionCount);

            topicObj.put("PartitionIds", Arrays.toString(topicPar.stream().map(p -> p.getId() + " ").collect(Collectors.toList()).toArray()));
            array.add(topicObj);
        }
        pageData.setData(array);
        return formatData(callback);
    }

}
