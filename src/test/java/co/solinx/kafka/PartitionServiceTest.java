package co.solinx.kafka;

import co.solinx.kafka.monitor.core.service.PartitionService;

/**
 * @author linxin
 * @version v1.0
 * Copyright (c) 2015 by solinx
 * @date 2017/12/13.
 */
public class PartitionServiceTest {


    public static void main(String[] args) {
        PartitionService service = new PartitionService();
        service.addPartition("2222", 3);
    }


}
