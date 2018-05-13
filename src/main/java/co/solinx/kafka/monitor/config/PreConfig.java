package co.solinx.kafka.monitor.config;

import co.solinx.kafka.monitor.api.InitBean;
import com.sun.org.apache.xml.internal.security.Init;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @auther linx
 * @create 2018-04-01 11:57
 **/
@Configuration
@ComponentScan("co.solinx.kafka.monitor.api")
public class PreConfig {

    @Bean
    InitBean initBean() {
        return new InitBean();
    }
}
