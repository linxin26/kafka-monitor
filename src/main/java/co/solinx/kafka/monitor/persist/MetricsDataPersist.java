package co.solinx.kafka.monitor.persist;

import co.solinx.kafka.monitor.common.DateUtils;
import co.solinx.kafka.monitor.model.KafkaMonitorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/30.
 */
public class MetricsDataPersist {

    //private static DBUtils dbUtils = new DBUtils(KafkaMonitorData.class);
    private static Logger logger = LoggerFactory.getLogger(MetricsDataPersist.class);

    public void toDB(KafkaMonitorData model) {
        String sql = "insert into kafkaMonitorData(producerTotal,consumerTotal,delay,duplicated,lostTotal,consumerError," +
                "producerError,delayMsAvg,delayMsMax,duplicatedRate,lostRate,delayedRate,currentTime,consumeAvailabilityAvg,produceAvailabilityAvg)" +
                " values(" + model.getProducerTotal() + "," +
                +model.getConsumerTotal() + "," +
                +model.getDelay() + "," +
                +model.getDuplicated() + "," +
                +model.getLostTotal() + "," +
                +model.getConsumerError() + "," +
                +model.getProducerError() + "," +
                +model.getDelayMsAvg() + "," +
                +model.getDelayMsMax() + "," +
                +model.getDuplicatedRate() + "," +
                +model.getLostRate() + "," +
                +model.getDelayedRate() + "," +
                "'" + DateUtils.getTimeStr(model.getCurrentTime(), DateUtils.HYPHEN_DISPLAY_DATE) + "'," +
                +model.getConsumeAvailabilityAvg()+ "," +
                +model.getProduceAvailabilityAvg()+ ")";
        try {
            //dbUtils.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("{}", e);
        }
    }
}
