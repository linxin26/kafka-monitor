package co.solinx.kafka;

import co.solinx.kafka.monitor.core.service.CustomConsumerGroupService;
import kafka.admin.ConsumerGroupCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/19.
 */
public class KafkaConsumer {


    static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    public static void main(String[] args) {
        List optionsList = new ArrayList<>();
        //optionsList.add()
//        ConsumerGroupCommand.ConsumerGroupCommandOptions commandOptions = new ConsumerGroupCommand.ConsumerGroupCommandOptions(new String[]{"--bootstrap-server=10.10.1.104:9093", "--group=test"});
//        ConsumerGroupCommand.ConsumerGroupService f = new ConsumerGroupCommand.KafkaConsumerGroupService(commandOptions);
//
//
//        f.list();
//        f.describeGroup("test2");

        //TestKafkaConsumerGroupService service = new TestKafkaConsumerGroupService();

        CustomConsumerGroupService service = new CustomConsumerGroupService();



        //logger.debug("consumerList {}", service.getConsumerList("test3"));
//        logger.debug("consumerList {}", service.getConsumerByTopic("foo2"));
        System.out.println(Arrays.toString(service.getConsumerList().toArray()));
    }


}
