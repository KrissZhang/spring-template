package com.self.common.config;

import com.self.common.constants.CfgConstants;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value(CfgConstants.REDIS_HOST)
    private String redisIp;

    @Value(CfgConstants.REDIS_PORT)
    private String redisPort;

    @Value(CfgConstants.REDIS_PASSWORD)
    private String redisPwd;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        SingleServerConfig singleServerConfig = config.useSingleServer();

        singleServerConfig.setAddress("redis://" + redisIp + ":" + redisPort);
        singleServerConfig.setPassword(redisPwd);

        return Redisson.create(config);
    }

}
