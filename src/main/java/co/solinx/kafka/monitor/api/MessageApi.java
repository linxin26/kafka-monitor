package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.MessageService;
import co.solinx.kafka.monitor.model.Message;
import com.alibaba.fastjson.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data/messageServlet")
public class MessageApi extends AbstractApi {

    @RequestMapping
    public String message( String topicName,
                          int partition,
                          int offset,
                          int messageSum,
                          String callback) {

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
