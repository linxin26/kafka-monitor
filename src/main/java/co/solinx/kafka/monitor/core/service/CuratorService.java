package co.solinx.kafka.monitor.core.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/14.
 */
public class CuratorService {

    static CuratorFramework curator = null;
    static JSONObject configObject = ConfigService.rootObj;

    private CuratorService() {

    }

    public static CuratorFramework getInstance() {
        if (curator == null) {
            JSONObject zkConfig = configObject.getJSONObject("zookeeper");
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
            builder.connectString(zkConfig.getString("host"));
            builder.sessionTimeoutMs(zkConfig.getInteger("SessionTimeoutMs"));
            builder.connectionTimeoutMs(zkConfig.getInteger("ConnectionTimeoutMs"));
            builder.retryPolicy(new RetryOneTime(zkConfig.getInteger("RetryOneTime")));

            curator = builder.build();
            curator.start();
        }
        return curator;
    }

}
