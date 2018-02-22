package co.solinx.kafka.monitor.api;

import co.solinx.kafka.monitor.core.service.MetricsReportService;
import com.alibaba.fastjson.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/metricsServlet")
public class MetricsApi extends AbstractApi {

    @GET
    @Path("{type}")
    public String metrics(@PathParam("type") String type,
                          @QueryParam("callback") String callback) {

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
