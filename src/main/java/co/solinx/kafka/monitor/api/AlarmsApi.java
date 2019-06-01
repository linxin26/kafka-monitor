package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.PageData;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 报警信息
 *
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@RestController
@RequestMapping("/data/alarmServlet")
public class AlarmsApi extends AbstractApi {

    private static KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();

    @RequestMapping
    public String alarms(String callback) {
        pageData = new PageData();
        try {
            List<Topic> topicList = service.getTopics();

            JSONArray array = new JSONArray();
            for (Topic
                    topic : topicList) {
                int preferred = (int) topic.getPreferred();
                if (preferred != 100) {
                    JSONObject temp = new JSONObject();
                    temp.put("preferred", preferred + "%");
                    temp.put("underReplicated", topic.getUnderReplicated());
                    temp.put("topic", topic.getName());
                    array.add(temp);
                }
            }
            pageData.setData(array);
        } catch (Exception e) {
            pageData.setStatus(500);
            pageData.setError(e.getMessage());
        }
        return formatData(callback, pageData);
    }

}
