package co.solinx.kafka.monitor;

import co.solinx.kafka.monitor.common.WebUI;
import co.solinx.kafka.monitor.core.service.ConfigService;
import co.solinx.kafka.monitor.core.service.InitService;

import java.util.Properties;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2016/12/12.
 */
public class Main {

    public static void main(String[] args) throws Exception {


        WebUI webUI = new WebUI(ConfigService.monitorConfig.getHost(), ConfigService.monitorConfig.getPort(), "WebUi", "/");

        webUI.bind();
        initMonitorService();
    }

    public static void initMonitorService() {
        InitService initService = new InitService();
        initService.init();
    }


}
