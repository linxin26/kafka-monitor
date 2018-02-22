package co.solinx.kafka.monitor.core.listener;

import co.solinx.kafka.monitor.model.Broker;
import com.alibaba.fastjson.JSON;
import kafka.utils.ZkUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by linx
 * @date 2018/1/26.
 */
public class BrokerListener implements PathChildrenCacheListener {

    private Logger logger = LoggerFactory.getLogger(BrokerListener.class);

    /**
     * broker信息缓存
     */
    public Map<Integer, Broker> brokerCache = new TreeMap();
    /**
     * broker节点缓存
     */
    private PathChildrenCache brokerPathCache;

    public BrokerListener(Map<Integer, Broker> brokerCache, PathChildrenCache brokerPathCache) {
        this.brokerCache = brokerCache;
        this.brokerPathCache = brokerPathCache;
    }

    /**
     * 添加broker
     *
     * @param broker
     */
    public void addBroker(Broker broker) {
        brokerCache.put(broker.getId(), broker);
    }

    /**
     * 移除broker
     *
     * @param brokerID
     */
    public void removeBroker(int brokerID) {
        brokerCache.remove(brokerID);
    }

    /**
     * 解析brokerID
     *
     * @param childData
     * @return
     */
    public int parseBrokerID(ChildData childData) {
        String brokerID = StringUtils.substringAfter(childData.getPath(), ZkUtils.BrokerIdsPath() + "/");
        return Integer.parseInt(brokerID);
    }

    /**
     * 解析Broker对象
     *
     * @param childData
     * @return
     */
    public Broker parseBroker(ChildData childData) {
        Broker broker = JSON.parseObject(childData.getData(), Broker.class);
        broker.setId(parseBrokerID(childData));
        return broker;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

        logger.debug("BrokerListener {} {}", event.getType(), event.getData());

        switch (event.getType()) {
            case CHILD_REMOVED:
                removeBroker(parseBrokerID(event.getData()));
                break;
            case CHILD_ADDED:
            case CHILD_UPDATED:
                addBroker(parseBroker(event.getData()));
                break;
            case INITIALIZED:
                brokerPathCache.getCurrentData().stream()
                        .map(BrokerListener.this::parseBroker)
                        .forEach(broker -> addBroker(broker));
                break;
            default:
                break;

        }

    }
}
