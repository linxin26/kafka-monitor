package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.CustomConsumerGroupService;
import co.solinx.kafka.monitor.model.Consumer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@Path("/consumerServlet")
public class ConsumerApi extends AbstractApi {

    @GET
    public String consumers(@QueryParam("callback") String callback) {
        CustomConsumerGroupService consumerGroupService = new CustomConsumerGroupService();
        List<Consumer> consumerList = consumers(consumerGroupService);


        pageData.setData(consumerList);
        return formatData(callback);
    }

    @GET
    @Path("{topicName}")
    public String consumersByTopicName(@PathParam("topicName") String topicName, @QueryParam("callback") String callback) {
        CustomConsumerGroupService consumerGroupService = new CustomConsumerGroupService();
        List<Consumer> consumerList = consumers(consumerGroupService, topicName);

        pageData.setData(consumerList);
        return formatData(callback);
    }

    public List<Consumer> consumers(CustomConsumerGroupService consumerGroupService) {
        return consumerGroupService.getConsumerList();
    }

    public List<Consumer> consumers(CustomConsumerGroupService consumerGroupService, String topicName) {
        return consumerGroupService.getConsumerList(topicName);
    }
}
