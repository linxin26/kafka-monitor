package co.solinx.kafka.monitor.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xin on 2016-12-18.
 */
public class InitService {

    private static  final Logger logger = LoggerFactory.getLogger(InitService.class);

    public void init() {
        logger.debug("init-------------");

        try {
            KafkaBaseInfoService.getInstance();
            monitorStart();

            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void monitorStart() throws IOException {
        ProduceService produceService = new ProduceService();
        ConsumerService consumerService = new ConsumerService();

        produceService.start();
        consumerService.start();


        MetricsReportService.getMetricsService().start();

        JolokiaService jolokiaService = new JolokiaService();
        jolokiaService.start();
    }
}
