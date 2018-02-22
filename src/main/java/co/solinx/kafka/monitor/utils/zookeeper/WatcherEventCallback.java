package co.solinx.kafka.monitor.utils.zookeeper;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/8/19.
 */
public interface WatcherEventCallback {

     void watchedEvent(WatcherEvent watched) throws Exception;
}
