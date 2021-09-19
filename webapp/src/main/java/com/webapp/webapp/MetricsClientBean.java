package com.webapp.webapp;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import com.webapp.webapp.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MetricsClientBean {

    private boolean publishMetrics = true;

    private String metricsServerHost = "localhost";

    private int metricsServerPort = 8125;

    private final static Logger logger = LoggerFactory.getLogger(MetricsClientBean.class);

    @Bean
    public StatsDClient statsDClient() {

        if (publishMetrics){
            logger.info("Using Non Blocking Client to Publish Metrics");
            return new NonBlockingStatsDClient("csye6225", metricsServerHost, metricsServerPort);
        }
        return new NoOpStatsDClient();
    }
}
