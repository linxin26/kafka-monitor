package co.solinx.kafka;

import co.solinx.kafka.monitor.model.Topic;
import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/16.
 */
public class TopicDataTest {

    static Logger logger= LoggerFactory.getLogger(TopicDataTest.class);
    static KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();

    public static void main(String[] args) throws InterruptedException {

        Thread.sleep(3000);
       // logger.debug("----------- {}", service.getBrokerById(0));

//        topic();
        topics();

    }

    public static void topics() throws InterruptedException {
//        while (true) {
            Thread.sleep(2000);
            JSONObject pageData=new JSONObject();
            List<Topic> topicList = service.getTopics();
            JSONArray array=new JSONArray();
            for (Topic
                    topic : topicList) {
                JSONObject temp=new JSONObject();
                double partitionSize= topic.getPartitionMap().size();

                temp.put("name",topic.getName());
                temp.put("partitionTotal",partitionSize);
//                double preferred=0;
//                for (Partition tPart :
//                        topic.getPartitionMap().values()) {
//                    if(tPart.getLeaderId()!=-1){
//                        preferred++;
//                    }
//                }
//                temp.put("preferred",preferred/partitionSize*100+"%");
//                temp.put("underReplicated",partitionSize-preferred);
                temp.put("preferred",topic.getPreferred()+"%");
                temp.put("underReplicated",topic.getUnderReplicated());
                JSONObject configObj= (JSONObject) topic.getConfig().get("config");

                temp.put("customConfig",configObj.size()>0?true:false);

                array.add(temp);
            }

            pageData.put("data", array);


            pageData.put("result", 200);

            logger.debug("----  topics {} ",topicList);
            logger.debug("----  pageData {} ",pageData);
//        }
    }
    public static void topic() throws InterruptedException {
        while (true) {
            Thread.sleep(3000);
            Topic topic= service.getTopic("testest");
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("PartitionTotal",topic.getPartitionMap().size());
            jsonObject.put("totalSize",topic.getSize());
            jsonObject.put("availableSize",topic.getAvailableSize());
            jsonObject.put("PreferredReplicas",topic.getPreferredReplicaPercent());
            jsonObject.put("UnderReplicatedPartitions",topic.getUnderReplicatedPartitions());

            logger.debug("-----------jsonObject  {}", jsonObject);
            logger.debug("-----------topic  {}", topic);
        }
    }
}
