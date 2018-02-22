package co.solinx.kafka;

import co.solinx.kafka.monitor.core.service.KafkaBaseInfoService;
import co.solinx.kafka.monitor.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/12.
 */
public class KafkaBaseInfoTest {

    static Logger logger = LoggerFactory.getLogger(KafkaBaseInfoTest.class);

    public static void main(String[] args) throws Exception {
      //  KafkaBaseInfoService kafka = KafkaBaseInfoService.getInstance();


//        List<Broker> brokersList = kafka.getBrokers();
//
//        for (Broker temp :
//                brokersList) {
//            logger.debug("{} {} ", temp, DateUtils.getTimeStr(new Date(temp.getTimestamp()), DateUtils.HYPHEN_DISPLAY_DATE));
//
//        }
//
//        List<Topic> topicList = kafka.getTopics();
//
//
//        for (Topic topic :
//                topicList) {
//            logger.debug("------ {}", topic);
//        }
//
//        logger.debug("{}", JSONArray.toJSONString(topicList));

//        CuratorFramework curator = CuratorService.getInstance();
//
//        TreeCache topicTreeCache = new TreeCache(curator, ZkUtils.BrokerIdsPath());
//        logger.debug("---- {}", ZkUtils.BrokerIdsPath());
//        topicTreeCache.start();
//        Thread.sleep(2000);
//
//
//        while (true) {
//            Map<String, ChildData> brokerMap = topicTreeCache.getCurrentChildren(ZkUtils.BrokerIdsPath());
//            logger.debug("====={}", brokerMap);
//            Thread.sleep(2000);
//        }


        KafkaBaseInfoService service = KafkaBaseInfoService.getInstance();


        while (true) {
            Thread.sleep(5000);
//            logger.debug("-----------brokerCache  {}", service.brokerCache);
//            service.getTopics();
        logger.debug("----------- {}", service.getBrokerById(0));
            Topic topic= service.getTopic("kafka-monitor-topic");
            logger.debug("-----------topic  {}", topic);
        }

//        MessageService messageService = new MessageService();
//        Thread.sleep(2000);
//        logger.debug("==== {}", messageService.getMesage("foo", 0, 0, 100));

    }

}
