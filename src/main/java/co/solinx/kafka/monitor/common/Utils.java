package co.solinx.kafka.monitor.common;

import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.Arrays;

/**
 * kafkaUtils
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/23.
 */
public class Utils {

    private static final Logger logger= LoggerFactory.getLogger(Utils.class);

    public static final int ZK_CONNECTION_TIMEOUT_MS = 30_000;
    public static final int ZK_SESSION_TIMEOUT_MS = 30_000;


    public static int getPartitionNumByTopic(String zk,String topic){
        ZkUtils zkUtils=ZkUtils.apply(zk,ZK_SESSION_TIMEOUT_MS,ZK_CONNECTION_TIMEOUT_MS, JaasUtils.isZkSecurityEnabled());

        try {
            return zkUtils.getPartitionsForTopics(JavaConversions.asScalaBuffer(Arrays.asList(topic))).apply(topic).size();
        }finally {
            zkUtils.close();
        }
    }

}
