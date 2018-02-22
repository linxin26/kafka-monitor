package co.solinx.kafka;

import co.solinx.kafka.monitor.model.Topic;
import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import co.solinx.kafka.monitor.model.Partition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * Created by xin on 2016-12-16.
 */
public class PartitionDataTest {

    static Logger logger = LoggerFactory.getLogger(PartitionDataTest.class);
    static KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();


    public static void main(String[] args) throws InterruptedException {

        topics("foo");

    }

    public static void topics(String topicName) throws InterruptedException {

        while (true) {
            Thread.sleep(2000);
            Topic topic = service.getTopic(topicName);
            JSONObject pageData = new JSONObject();
            JSONArray array = new JSONArray();
            //Collection<TopicPartition> topicPar = topic.getLeaderPartitions(brokerID);

            for (Partition tp :
                    topic.getPartitionMap().values()) {
                JSONObject part = new JSONObject();
                part.put("id", tp.getId());
                part.put("firstOffset", tp.getFirstOffset());
                part.put("lastOffset", tp.getSize());
                part.put("size", tp.getSize() - tp.getFirstOffset());
                part.put("Leader", tp.getLeader() != null ? tp.getLeader().getId() : "");

                part.put("inSyncReplicas", tp.getInSyncReplicas().stream().map(p->p.getId()).collect(Collectors.toList()));
                part.put("leaderPreferred", tp.isLeaderPreferred());
                part.put("underReplicated", tp.isUnderReplicated());
                array.add(part);
            }

            pageData.put("data", array);

            logger.debug("----  pageData {} ", pageData);
        }
    }
}
