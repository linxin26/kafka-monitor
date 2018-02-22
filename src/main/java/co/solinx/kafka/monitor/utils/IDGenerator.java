package co.solinx.kafka.monitor.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/10/24.
 */
public class IDGenerator {

    private int start = 0;
    private int end;
    private AtomicInteger id;

    private IDGenerator(int end, int start) {
        this.end = end;
        this.start = start;
        id = new AtomicInteger(this.start);
    }


    public int getGeneratorID() {
        if (id.get() > this.end) {
            id.set(0);
        }
        return id.getAndIncrement();
    }

    public static void main(String[] args) throws InterruptedException {
        IDGenerator generator = new IDGenerator(0, 255);

        while (true) {
            Thread.sleep(1000);
            System.out.println(generator.getGeneratorID());
        }
    }

}
