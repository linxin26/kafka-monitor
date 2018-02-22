package co.solinx.kafka.monitor.utils.zookeeper;

import lombok.Data;
import org.apache.zookeeper.Watcher;

import java.util.Arrays;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/10/21.
 */
@Data
public class WatcherEvent {

    private String path;
    private byte[] data;
    private Watcher.Event.EventType eventType;
    private List<String> childrenNode;
    private Watcher.Event.KeeperState state;
    private List<String> oldChildrenNode;


    @Override
    public String toString() {
        return "WatcherEvent{" +
                "childrenNode=" + childrenNode +
                ", path='" + path + '\'' +
                ", data=" + Arrays.toString(data) +
                ", eventType=" + eventType +
                ", state=" + state +
                ", oldChildrenNode=" + oldChildrenNode +
                '}';
    }
}
