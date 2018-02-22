package co.solinx.kafka.monitor.model;


import lombok.Data;

import java.util.Date;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/29.
 */
@Data
public class KafkaMonitorData {

    private long id;
    private double producerTotal;
    private double consumerTotal;
    private double delay;
    private double duplicated;
    private double lostTotal;
    private double consumerError;
    private double producerError;
    private double delayMsAvg;
    private double delayMsMax;
    private double duplicatedRate;
    private double lostRate;
    private double delayedRate;
    private double consumeAvailabilityAvg;
    private double produceAvailabilityAvg;
    private Date currentTime;




    public void setDelayMsAvg(double delayMsAvg) {
        if (Double.NEGATIVE_INFINITY == delayMsAvg || Double.POSITIVE_INFINITY == delayMsAvg) {
            this.delayMsAvg = 0;
        } else {
            this.delayMsAvg = delayMsAvg;
        }
    }

    public double getDelayMsMax() {
        return delayMsMax;
    }

    public void setDelayMsMax(double delayMsMax) {
        if (Double.NEGATIVE_INFINITY == delayMsMax || Double.POSITIVE_INFINITY == delayMsMax) {
            this.delayMsMax = 0;
        } else {
            this.delayMsMax = delayMsMax;
        }
    }



    @Override
    public String toString() {
        return "KafkaMonitorData{" +
                "id=" + id +
                ", producerTotal=" + producerTotal +
                ", consumerTotal=" + consumerTotal +
                ", delay=" + delay +
                ", duplicated=" + duplicated +
                ", lostTotal=" + lostTotal +
                ", consumerError=" + consumerError +
                ", producerError=" + producerError +
                ", delayMsAvg=" + delayMsAvg +
                ", delayMsMax=" + delayMsMax +
                ", duplicatedRate=" + duplicatedRate +
                ", lostRate=" + lostRate +
                ", delayedRate=" + delayedRate +
                ", consumeAvailabilityAvg=" + consumeAvailabilityAvg +
                ", produceAvailabilityAvg=" + produceAvailabilityAvg +
                ", currentTime=" + currentTime +
                '}';
    }
}
