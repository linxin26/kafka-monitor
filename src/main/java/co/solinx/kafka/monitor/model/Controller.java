package co.solinx.kafka.monitor.model;

import lombok.Data;

import java.util.Date;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by linx
 * @date 2017/12/12.
 */

@Data
public class Controller {

    /**
     * 版本
     */
    private int version;
    /**
     * brokerId
     */
    private int brokerId;
    private Date timestamp;
}
