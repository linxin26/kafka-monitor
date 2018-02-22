package co.solinx.kafka.monitor.utils.zookeeper;

import co.solinx.kafka.monitor.utils.JsonLoader;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/10/20.
 */
public class ZookeeperClient {

    Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);

    private CuratorFramework curator;
    private static ZookeeperClient zkClient = null;

    /**
     * @param confPath json格式Zk配置文件路径
     */
    public ZookeeperClient(String confPath) {
        this.initZookeeper(confPath, true);
    }

    public static ZookeeperClient getInstance() {
        if (zkClient == null) {
            zkClient = new ZookeeperClient(ZookeeperClient.class.getClassLoader().getResourceAsStream("zkConfig.json"),false);
        }
        return zkClient;
    }

    /**
     * @param confPath json格式Zk配置文件路径
     * @param isAsync  是否异步，默认异步
     */
    public ZookeeperClient(String confPath, boolean isAsync) {
        this.initZookeeper(confPath, isAsync);
    }

    /**
     * 使用默认配置文件
     */
    public ZookeeperClient() {
        this.initZookeeper("", false);
    }


    public ZookeeperClient(InputStream stream, boolean isAsync) {
        this.initZookeeper(stream, isAsync);
    }

    public ZookeeperClient(InputStream stream) {
        this.initZookeeper(stream, true);
    }

    /**
     * 初始化zk
     *
     * @param confPath
     */
    private void initZookeeper(String confPath, boolean isAsync) {

        JSONObject zkConfig = loadZkConf(confPath);

        buildCurator(zkConfig, isAsync);
    }

    /**
     * 初始化zk
     *
     * @param inputStream
     */
    private void initZookeeper(InputStream inputStream, boolean isAsync) {

        JSONObject zkConfig = loadZkConf(inputStream);

        buildCurator(zkConfig, isAsync);
    }

    private void buildCurator(JSONObject zkConfig, boolean isAsync) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString(zkConfig.getString("host"));
        builder.sessionTimeoutMs(zkConfig.getInteger("SessionTimeoutMs"));
        builder.connectionTimeoutMs(zkConfig.getInteger("ConnectionTimeoutMs"));
        builder.retryPolicy(new RetryOneTime(zkConfig.getInteger("RetryOneTime")));
        curator = builder.build();
        curator.start();
        if (!isAsync) {
            waitingConnect();
        }
    }

    /**
     * 等待连接
     */
    private void waitingConnect() {
        while (!checkConnect()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("{}", e);
            }
        }
    }

    private void reconnectProcess() {
        curator.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
            }
        });
    }

    /**
     * 添加Connect状态监听器
     *
     * @param listener
     */
    public void addConnectionStateListener(ConnectionStateListener listener) {
        curator.getConnectionStateListenable().addListener(listener);
    }


    private JSONObject loadZkConf(String path) {
        JSONObject zkConfig = JsonLoader.loadJSONFile(path);
        if (zkConfig.size() == 0) {
            zkConfig = defaultConfig();
        }
        return zkConfig;
    }

    private JSONObject loadZkConf(InputStream inputStream) {
        JSONObject zkConfig = JsonLoader.loadJSONFile(inputStream);
        if (zkConfig.size() == 0) {
            zkConfig = defaultConfig();
        }
        return zkConfig;
    }

    private JSONObject defaultConfig() {
        JSONObject zkConfig = new JSONObject();
        zkConfig.put("host", "127.0.0.1:2181");
        zkConfig.put("SessionTimeoutMs", "3000");
        zkConfig.put("ConnectionTimeoutMs", "3000");
        zkConfig.put("RetryOneTime", "3000");
        logger.debug(" load default zkConfig {}", zkConfig);
        return zkConfig;
    }

    /**
     * 检查节点是否存在
     *
     * @param path
     * @return
     */
    public boolean checkExists(String path) {
        boolean exists = false;
        try {
            if (checkConnect()) {
                Stat stat = curator.checkExists().forPath(path);
                if (stat != null) {
                    exists = true;
                }
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return exists;
    }

    /**
     * 创建节点
     *
     * @param path 路径
     * @param mode 节点类型
     * @throws Exception
     */
    public void createNode(String path, CreateMode mode) throws Exception {
        curator.create().withMode(mode).forPath(path);
    }

    /**
     * 创建节点，默认为临时节点
     *
     * @param path 路径
     * @throws Exception
     */
    public void createNode(String path) throws Exception {
        curator.create().forPath(path);

    }

    /**
     * 递归创建节点
     *
     * @param path 路径
     * @throws Exception
     */
    public void createContainers(String path) throws Exception {
        curator.createContainers(path);
    }

    /**
     * 创建节点
     *
     * @param path 路径
     * @param data 节点中数据
     * @throws Exception
     */
    public void createNode(String path, String data) throws Exception {
        curator.create().forPath(path, data.getBytes());
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @throws Exception
     */
    public void deleteNode(String path) throws Exception {
        List<String> childs = getChildrenFullPath(path);
        for (int i = 0; i < childs.size(); i++) {
            deleteNode(childs.get(i));
        }
        curator.delete().forPath(path);
    }

    /**
     * 取得子节点
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    public List<String> getChildren(String path) throws Exception {
        if (!checkExists(path)) {
            createNode(path);
        }
        return curator.getChildren().forPath(path);
    }

    /**
     * 以绝对路径返回子节点
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> getChildrenFullPath(String path) throws Exception {
        List<String> pathList = new ArrayList();
        if (checkExists(path)) {
            if (checkConnect()) {
                pathList = curator.getChildren().forPath(path);
                for (int i = 0; i < pathList.size(); i++) {
                    String temp = pathList.get(i);
                    temp = path + "/" + temp;
                    pathList.set(i, temp);
                }
            }
        }
        return pathList;
    }

    /**
     * 创建节点
     *
     * @param path 路径
     * @param mode 模式
     * @param data 数据
     * @throws Exception
     */
    public void createNode(String path, CreateMode mode, String data) throws Exception {
        curator.create().withMode(mode).forPath(path, data.getBytes());
    }

    /**
     * 检查链接
     *
     * @return
     */
    public boolean checkConnect() {
        return curator.getZookeeperClient().isConnected();
    }

    /**
     * 关闭连接
     */
    public void close() {
        logger.debug("--------zoo close");
        if (curator.getState() == CuratorFrameworkState.STARTED) {
            curator.close();
        }
    }

    /**
     * 开启
     */
    public void start() {
        curator.start();
    }


    /**
     * 取得节点数据
     *
     * @param path 路径
     * @return
     * @throws Exception
     */
    public byte[] getData(String path) throws Exception {
        byte[] result = new byte[]{};
        if (checkExists(path)) {
            result = curator.getData().forPath(path);
        }
        return result;
    }

    /**
     * 设置节点data值
     *
     * @param path
     * @param data
     * @throws Exception
     */
    public void setData(String path, String data) throws Exception {
        if (checkConnect()) {
            curator.setData().forPath(path, data.getBytes());
        }
    }

    /**
     * 监听该路径下得子节点
     *
     * @param path     路径
     * @param callback 监听回调
     * @throws Exception
     */
    public void watcherChildrenNode(final String path, final WatcherEventCallback callback) throws Exception {

        final List<String> oldChildren = this.getChildren(path);
        logger.debug("oldChildren------{}", oldChildren);
        CuratorWatcher watcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                try {
                    WatcherEvent watcherEvent = new WatcherEvent();
                    watcherEvent.setState(event.getState());
                    watcherEvent.setPath(event.getPath());
                    watcherEvent.setEventType(event.getType());
                    watcherEvent.setOldChildrenNode(oldChildren);

                    callback.watchedEvent(watcherEvent);
                    if (checkExists(path)) {
                        watcherChildrenNode(path, callback);
                    }
                } catch (Exception e) {
                    logger.debug("{}", e.getMessage());
                }
            }
        };
        curator.getChildren().usingWatcher(watcher).forPath(path);
    }

    /**
     * 监听多个节点下的子节点
     *
     * @param pathList 路径List
     * @param callback 回调函数
     */
    public void watcherChildrenNodes(List<String> pathList, final WatcherEventCallback callback) throws Exception {

        for (final String path :
                pathList) {
            this.watcherChildrenNode(path, callback);
        }
    }

    /**
     * 监听节点数据
     *
     * @param path
     * @param callback
     * @throws Exception
     */
    public void watcherData(final String path, final WatcherEventCallback callback) throws Exception {

        CuratorWatcher watcherData = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                try {
                    WatcherEvent watcherEvent = new WatcherEvent();
                    watcherEvent.setState(event.getState());
                    watcherEvent.setPath(event.getPath());
                    watcherEvent.setEventType(event.getType());


                    callback.watchedEvent(watcherEvent);
                    watcherData(path, callback);
                } catch (Exception e) {
                    logger.debug("{}", e.getMessage());
                }
            }
        };
        if (checkExists(path)) {
            curator.getData().usingWatcher(watcherData).forPath(path);
        }
    }
}
