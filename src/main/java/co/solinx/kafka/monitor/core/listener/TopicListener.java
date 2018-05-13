package co.solinx.kafka.monitor.core.listener;

import co.solinx.kafka.monitor.model.Partition;
import co.solinx.kafka.monitor.model.Topic;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kafka.utils.ZkUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.util.List;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.NODE_ADDED;


public class TopicListener implements TreeCacheListener{


    private List<Topic> topicList;

    public TopicListener(List<Topic> topicList) {
        this.topicList = topicList;
    }

    @Override
    public void childEvent(CuratorFramework curator, TreeCacheEvent event) throws Exception {
        ChildData data = event.getData();
        if (data != null) {
            if (event.getType() == NODE_ADDED) {

            }
            String path = data.getPath();
            //判断是否为topics节点
            if (path.contains(String.format("%s/",ZkUtils.BrokerTopicsPath())) && (!path.contains("partitions"))) {
                Topic topic = JSONObject.parseObject(data.getData(), Topic.class);
                String name = path.substring(path.lastIndexOf("/") + 1, path.length());
                topic.setName(name);

                int[] tPartiyions = topic.getPartitions().keySet().stream().mapToInt((t) -> Integer.valueOf(t)).sorted().toArray();
                for (Object key : tPartiyions
                        ) {
                    String partitionPath = String.format("%s/partitions/%s/state", path, key);
                    String state = new String(curator.getData().forPath(partitionPath));
                    Partition partition = JSONObject.parseObject(state, Partition.class);
                    JSONArray replicas = topic.getPartitions().getJSONArray(String.valueOf(key));
                    int[] replicasArray = new int[replicas.size()];
                    for (int i = 0; i <
                            replicas.size(); i++) {
                        replicasArray[i] = replicas.getInteger(i);
                    }
                    partition.setReplicasArray(replicasArray);

                    topic.getPartitionMap().put((Integer) key, partition);
                }
                topicList.add(topic);
            }
        }
    }
}
