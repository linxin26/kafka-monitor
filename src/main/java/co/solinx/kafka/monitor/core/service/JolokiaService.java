package co.solinx.kafka.monitor.core.service;

import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JvmAgentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 *
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/12/22.
 */
public class JolokiaService {

    private Logger logger = LoggerFactory.getLogger(JolokiaService.class);
    private JolokiaServer jolokiaServer;

    public JolokiaService() throws IOException {
        jolokiaServer = new JolokiaServer(new JvmAgentConfig("host=*,port=8889"), false);

    }

    public void start() {
        jolokiaServer.start();
        logger.info("Jolokia Server started at port {}", 8889);
    }

}
