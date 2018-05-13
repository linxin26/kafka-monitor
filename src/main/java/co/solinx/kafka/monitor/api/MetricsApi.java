package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.MetricsReportService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/data/metricsServlet")
public class MetricsApi extends AbstractApi {

    @RequestMapping("/{type}")
    public String metrics(@PathVariable("type") String type,
                          String callback) {

        MetricsReportService metricsServlet = MetricsReportService.getMetricsService();
        JSONObject resultList = new JSONObject();

        switch (type) {
            case "total":
                resultList = metricsServlet.getTotalMetrics();
                break;
            case "avg":
                resultList = metricsServlet.getAvgMetrics();
                break;
            case "delayed":
                resultList = metricsServlet.getDelayedMetrics();
                break;
            case "error":
                resultList = metricsServlet.getErrorMetrics();
                break;
            case "rate":
                resultList = metricsServlet.getRateMetrics();
                break;
            case "delay":
                resultList = metricsServlet.getDelayMetrics();
                break;
            default:
                break;
        }

        pageData.setData(resultList);


        return formatData(callback);
    }

}
