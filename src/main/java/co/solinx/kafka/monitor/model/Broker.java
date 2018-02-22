package co.solinx.kafka.monitor.model;

import co.solinx.kafka.monitor.common.DateUtils;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/12.
 */
@Data
public class Broker {

    private int id;
    private String[] endpoints;
    private int jmx_port;
    private int port;
    private String host;
    private int version;
    private Date timestamp;
    /**
     * 集群控制者
     */
    private boolean controller;


    public String getStartTime() {
        return DateUtils.getTimeStr(timestamp, DateUtils.HYPHEN_DISPLAY_DATE);
    }

    @Override
    public String toString() {
        return "Broker{" +
                "id=" + id +
                ", endpoints=" + Arrays.toString(endpoints) +
                ", jmx_port=" + jmx_port +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }
}
