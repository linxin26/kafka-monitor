package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/27.
 */
@RestController
@RequestMapping("/data/partitionServlet")
public class PartitionsApi extends AbstractApi {

    private KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();


    @RequestMapping
    public String partition(String topicName, String callback) {

        Topic topic = service.getTopic(topicName);
        JSONArray array = new JSONArray();
        for (Partition tp :
                topic.getPartitionMap().values()) {
            JSONObject part = new JSONObject();
            part.put("id", tp.getId());
            part.put("firstOffset", tp.getFirstOffset());
            part.put("lastOffset", tp.getSize());
            part.put("size", tp.getSize() - tp.getFirstOffset());
            //是否leader
            part.put("leader", tp.getLeaderId());
            //副本集
            part.put("replicas", Arrays.toString(tp.getReplicasArray()));
            //同步副本
            part.put("inSyncReplicas", Arrays.toString(tp.getIsr()));
            //首先副本是否为leader
            part.put("leaderPreferred", String.valueOf(tp.isLeaderPreferred()));
            //是否复制中
            part.put("underReplicated", String.valueOf(tp.isUnderReplicated()));
            array.add(part);
        }
        pageData.setData(array);

        return formatData(callback);
    }
}
