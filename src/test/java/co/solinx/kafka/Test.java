package co.solinx.kafka;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

/**
 * Created by xin on 2016-12-26.
 */
public class Test {


    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(1482759606);
        System.out.println(timestamp.toLocalDateTime());
        System.out.println(new Time(1482759113));

        System.out.println(new Random().nextInt(3));
    }

}
