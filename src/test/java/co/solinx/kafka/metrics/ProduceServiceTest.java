package co.solinx.kafka.metrics;

import co.solinx.kafka.monitor.core.service.ConsumerService;
import co.solinx.kafka.monitor.core.service.ProduceService;
import co.solinx.kafka.monitor.core.service.JolokiaService;

import java.io.IOException;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/22.
 */
public class ProduceServiceTest {

    public static void main(String[] args) throws IOException {
        ProduceService produceService = new ProduceService();
        ConsumerService consumerService = new ConsumerService();

        produceService.start();
        consumerService.start();


//        MetricsReportService reportService = new MetricsReportService();
//        reportService.start();

        JolokiaService jolokiaService = new JolokiaService();
        jolokiaService.start();
    }

}
