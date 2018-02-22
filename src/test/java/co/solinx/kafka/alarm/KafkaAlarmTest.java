package co.solinx.kafka.alarm;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/25.
 */
public class KafkaAlarmTest {

    static KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();

    public static void main(String[] args) {
        List<Topic> topicList = service.getTopics();

        JSONArray array = new JSONArray();
        for (Topic
                topic : topicList) {
//            int preferred=0;
//            int partitionSize= topic.getPartitionMap().size();
//            for (Partition tPart :
//                    topic.getPartitionMap().values()) {
//                if(tPart.getLeaderId()!=-1){
//                    preferred++;
//                }
//            }
//            if(preferred!=1) {
//                JSONObject temp = new JSONObject();
//                temp.put("preferred", preferred / partitionSize * 100 + "%");
//                temp.put("underReplicated", partitionSize - preferred);
//                temp.put("topic", topic.getName());
//                array.add(temp);
//            }
            int preferred = (int) topic.getPreferred();
            if (preferred != 100) {
                JSONObject temp = new JSONObject();
                temp.put("preferred", preferred + "%");
                temp.put("underReplicated", topic.getUnderReplicated());
                temp.put("topic", topic.getName());
                array.add(temp);
            }
        }
        System.out.println(array.toJSONString());
    }


}
