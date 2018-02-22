package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.MessageService;
import co.solinx.kafka.monitor.model.Message;
import com.alibaba.fastjson.JSONArray;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/messageServlet")
public class MessageApi extends AbstractApi {

    @GET
    public String message(@QueryParam("topic") String topicName,
                          @QueryParam("partition") int partition,
                          @QueryParam("offset") int offset,
                          @QueryParam("messageSum") int messageSum,
                          @QueryParam("callback") String callback) {

        MessageService service = new MessageService();

        List<Message> messageList = service.getMesage(topicName, partition, offset, messageSum);
        JSONArray array = new JSONArray();
        for (Message message :
                messageList) {
            array.add(message);
        }
        pageData.setData(array);
        return formatData(callback);
    }

}
