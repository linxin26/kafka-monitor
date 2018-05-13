package co.solinx.kafka.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author linx
 * @create 2018-04 01-0:33
 **/
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Start {

    public static void main(String[] args) {
        SpringApplication.run(Start.class);
    }

}
