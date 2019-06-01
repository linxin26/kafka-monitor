package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.CustomConsumerGroupService;
import co.solinx.kafka.monitor.model.Consumer;
import co.solinx.kafka.monitor.model.PageData;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@RestController
@RequestMapping("/data/consumerServlet")
public class ConsumerApi extends AbstractApi {

    @RequestMapping
    public String consumers(String callback) {
        pageData = new PageData();
        CustomConsumerGroupService consumerGroupService = new CustomConsumerGroupService();
        List<Consumer> consumerList = consumers(consumerGroupService);
        pageData.setData(consumerList);
        return formatData(callback, pageData);
    }

    @RequestMapping(value = "/{topicName}", method = RequestMethod.GET)
    public String consumersByTopicName(@PathVariable String topicName, String callback) {
        pageData = new PageData();
        CustomConsumerGroupService consumerGroupService = new CustomConsumerGroupService();
        List<Consumer> consumerList = consumers(consumerGroupService, topicName);

        pageData.setData(consumerList);
        return formatData(callback, pageData);
    }

    public List<Consumer> consumers(CustomConsumerGroupService consumerGroupService) {
        return consumerGroupService.getConsumerList();
    }

    public List<Consumer> consumers(CustomConsumerGroupService consumerGroupService, String topicName) {
        return consumerGroupService.getConsumerList(topicName);
    }
}
