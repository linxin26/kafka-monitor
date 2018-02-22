package co.solinx.kafka.monitor.model;

import lombok.Data;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/15.
 */
@Data
public class Message {

    /**
     * value
     */
    private String message;
    /**
     * key
     */
    private String key;
    private boolean valid;
    private long checksum;
    private String compressionCodec;


    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", key='" + key + '\'' +
                ", valid=" + valid +
                ", checksum=" + checksum +
                ", compressionCodec='" + compressionCodec + '\'' +
                '}';
    }
}
