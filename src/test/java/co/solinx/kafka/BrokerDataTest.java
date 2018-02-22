package co.solinx.kafka;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import co.solinx.kafka.monitor.model.Broker;
import co.solinx.kafka.monitor.model.Topic;
import co.solinx.kafka.monitor.model.Partition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/16.
 */
public class BrokerDataTest {

    static Logger logger= LoggerFactory.getLogger(BrokerDataTest.class);
    static KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();


    public static void main(String[] args) throws InterruptedException {
        brokers();
        //broker(0);
//        topics(0);
    }



    public static void brokers() throws InterruptedException {
        while (true) {
            Thread.sleep(2000);
            JSONObject pageData=new JSONObject();
            List<Broker> brokerList = service.getBrokers();
            JSONArray array=new JSONArray();

            pageData.put("data", brokerList);


            pageData.put("result", 200);

            logger.debug("----  pageData {} ",pageData);
        }
    }

    public static void broker(int id) throws InterruptedException {

        while (true) {
            Thread.sleep(2000);
            Broker broker = service.getBrokerById(id);
            List<Topic> topicList = service.getTopics();
            JSONObject pageData = new JSONObject();
            int partitionCount = 0;
            for (Topic topic :
                    topicList) {
                partitionCount += topic.getLeaderPartitions(id).size();
            }
            pageData.put("data", broker);
            pageData.put("partitionCount", partitionCount);
            pageData.put("topicCount", topicList.size());


            logger.debug("----  pageData {} ", pageData);
        }
    }

    public static void topics(int brokerID) throws InterruptedException {

        while (true) {
            Thread.sleep(2000);
            List<Topic> topicList = service.getTopics();
            JSONObject pageData = new JSONObject();
            JSONArray array=new JSONArray();
            for (Topic topic :
                    topicList) {
                Collection<Partition> topicPar=topic.getLeaderPartitions(brokerID);
                int partitionCount = topicPar.size();
                JSONObject topicObj=new JSONObject();
                topicObj.put("name",topic.getName());
                topicObj.put("partitionCount",topic.getPartitionMap().size());
                topicObj.put("brokerPartitionCount", partitionCount);
                List<Integer> idArray=new ArrayList<>();
                for (Partition partition :
                        topicPar) {
                    idArray.add(partition.getId());
                }
                topicObj.put("PartitionIds",idArray);
                array.add(topicObj);
            }
            pageData.put("data", array);

            logger.debug("----  pageData {} ", pageData);
        }
    }

}
