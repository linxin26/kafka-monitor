package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Broker;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * broker信息
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/26.
 */

@Path("/brokerServlet")
public class BrokersApi extends AbstractApi {

    private Logger logger = LoggerFactory.getLogger(BrokersApi.class);
    private KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();

    @GET
    public String brokers(@QueryParam("callback") String callback) {
        List<Broker> brokerList = service.getBrokers();

        pageData.setData(brokerList);

        return formatData(callback);
    }

    @GET
    @Path("{id}")
    public String getBrokerById(@PathParam("id") int id, @QueryParam("callback") String callback) {
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

        return formatData(callback);
    }

    @GET
    @Path("summary")
    public String getSummary(@QueryParam("callback") String callback) {

        List<Broker> brokerList = service.getBrokers();
        JSONObject result = new JSONObject();
        result.put("brokerTotal", brokerList.size());
        result.put("brokerAbleTotal", brokerList.size());

        pageData.setData(result);

        return formatData(callback);
    }

}
