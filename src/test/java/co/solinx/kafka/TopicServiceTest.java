package co.solinx.kafka;

import co.solinx.kafka.monitor.core.service.TopicService;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/13.
 */
public class TopicServiceTest {

    public void topicCreate() {
        TopicService topicService = new TopicService();
        topicService.createTopic("test", 2, 2);
    }


    public void deleteTopic() {
        TopicService topicService = new TopicService();
        topicService.deleteTopic("test");
    }

    public static void main(String[] args) {
        TopicServiceTest test = new TopicServiceTest();
        test.topicCreate();
//        test.deleteTopic();
    }


}
