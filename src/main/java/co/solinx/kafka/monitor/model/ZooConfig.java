package co.solinx.kafka.monitor.model;

import lombok.Data;

/**
 * zookeeper config
 *
 * @author linx
 * @create 2018-02 06-22:39
 **/
@Data
public class ZooConfig {

    public static String HOST="host";
    public static String SESSION_TIMEOUT_MS="SessionTimeoutMs";
    public static String CONNECTION_TIMEOUT_MS="ConnectionTimeoutMs";
    public static String RETRY_ONE_TIME="RetryOneTime";
    private String host;
    private String sessionTimeoutMs;
    private String connectionTimeoutMs;
    private String retryOneTime;

}
