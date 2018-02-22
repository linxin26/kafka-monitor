package co.solinx.kafka.monitor.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Kafka monitor config
 *
 * @author linx
 * @create 2018-02 07-22:38
 **/
@Data
public class MonitorConfig {

    @JSONField(name = "host")
    private String host;
    @JSONField(name = "port")
    private int port;
    @JSONField(name = "monitorTopic")
    private String monitorTopic;
}
