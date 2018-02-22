package co.solinx.kafka.monitor.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by linx
 * @date 2018/1/31.
 */
@Data
public class PageData {

    private Object data;
    private int status;
    private String error;
    private JSONObject extend;


    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("data", data);
        result.put("status", status);
        result.put("error", error);

        result.putAll(extend);

        return result.toJSONString();
    }

    public static void main(String[] args) {
        PageData data = new PageData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", 0);
        data.setData(jsonObject);
        data.setStatus(200);
    }
}
