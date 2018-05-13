package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.InitService;
import org.springframework.beans.factory.InitializingBean;

/**
 * @auther linx
 * @create 2018-04-01 11:47
 **/
public class InitBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        InitService initService = new InitService();
        initService.init();
    }
}
