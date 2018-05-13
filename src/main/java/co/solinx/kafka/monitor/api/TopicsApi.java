package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.core.service.TopicService;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@RestController
@RequestMapping("/data/topicServlet")
public class TopicsApi extends AbstractApi {

    private KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();
    private TopicService topicService = new TopicService();
    private final static Logger logger = LoggerFactory.getLogger(TopicsApi.class);

    @RequestMapping
    public String topics(String callback) {
        List<Topic> topicList = service.getTopics();

        JSONArray array = new JSONArray();
        for (Topic
                topic : topicList) {
            JSONObject temp = new JSONObject();
            double partitionSize = topic.getPartitionMap().size();

            temp.put("name", topic.getName());
            temp.put("partitionTotal", partitionSize);

            //partition首选副本率（首选副本为leader），最优为100%
            temp.put("preferred", topic.getPreferred());
            //正在复制的Partition数，正常应为0
            temp.put("underReplicated", topic.getUnderReplicated());
            JSONObject configObj = (JSONObject) topic.getConfig().get("config");

            temp.put("customConfig", configObj.size() > 0 ? true : false);

            array.add(temp);
        }

        pageData.setData(array);
        return formatData(callback);
    }

    @RequestMapping("/summary")
    public String summary(String callback) {

        List<Topic> topicList = service.getTopics();
        JSONObject result = new JSONObject();
        result.put("topicTotal", topicList.size());
        result.put("partitionTotal", topicList.stream().mapToInt((t) -> t.getPartitionMap().size()).sum());

        pageData.setData(result);

        return formatData(callback);
    }

    @RequestMapping(value = "/{topicName}", method = RequestMethod.GET)
    public String topic(@PathVariable String topicName, String callback) {

        Topic topic = service.getTopic(topicName);
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name", topic.getName());
        jsonObject.put("PartitionTotal", topic.getPartitionMap().size());
        jsonObject.put("totalSize", topic.getSize());
        jsonObject.put("availableSize", topic.getAvailableSize());
        jsonObject.put("PreferredReplicas", topic.getPreferredReplicaPercent() * 100 + "%");
        jsonObject.put("UnderReplicatedPartitions", Arrays.toString(topic.getUnderReplicatedPartitions().stream().mapToInt(p -> p.getId()).toArray()));

        pageData.setData(jsonObject);

        return formatData(callback);
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(String topic,
                         int replicaFactor,
                         int partitions, String callback) {

        try {
            topicService.createTopic(topic, Integer.valueOf(partitions)
                    , replicaFactor);

        } catch (Exception e) {
            pageData.setStatus(500);
            pageData.setError(e.getMessage());
            logger.error("添加topic异常", e);
        }

        return formatData(callback);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(String topic, String callback) {
        topicService.deleteTopic(topic);
        return formatData(callback);
    }
}
