package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.model.PageData;
import com.alibaba.fastjson.JSONObject;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/29.
 */
public abstract class AbstractApi {
    protected PageData pageData = new PageData();

    public AbstractApi() {
        pageData.setStatus(200);
        pageData.setExtend(new JSONObject());
    }

    public String formatData(String callback) {
        String resultStr = pageData.toString();
        if (callback != null) {
            resultStr = String.format("%s(%s)", callback, resultStr);
        }
        return resultStr;
    }

}
