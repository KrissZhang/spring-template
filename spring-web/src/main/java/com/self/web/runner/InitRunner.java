package com.self.web.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class InitRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("service startup completed...");
    }

}
