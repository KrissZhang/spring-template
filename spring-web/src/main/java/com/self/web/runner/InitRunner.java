package com.self.web.runner;

import cn.hutool.core.net.NetUtil;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.self.biz.delayqueue.RedisDelayQueueHandler;
import com.self.common.constants.CacheConstants;
import com.self.common.enums.RedisDelayQueueEnum;
import com.self.common.utils.RedisUtils;
import com.self.common.utils.RedissonUtils;
import com.self.common.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Order(0)
public class InitRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitRunner.class);

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedissonUtils redissonUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 雪花漂移id初始化
        yitIdInit();

        // 延迟队列侦听
        delayQueueListen();

        logger.info("service startup completed...");
    }

    /**
     * 雪花漂移id初始化
     */
    private void yitIdInit(){
        // 获取mac地址
        String macAddress = NetUtil.getLocalhost().getHostAddress();

        logger.info("{} 配置分布式Id Work缓存========开始", macAddress);

        boolean existWorkerId = redisUtils.hHasKey(CacheConstants.YITTER_ID_IP_KEY, macAddress);
        // 若已存在缓存则跳过不设置
        if(existWorkerId) {
            logger.info("{} 已配置分布式Id Work...", macAddress);
            return;
        }

        try {
            // 分布式锁等待120秒，执行时长最大120秒
            boolean locked = redissonUtils.tryLock(CacheConstants.YITTER_ID_GENERATOR_KEY, 120, 120);
            if(!locked) {
                throw new RuntimeException(macAddress + " 设置分布式Id机器号失败");
            }

            boolean initWorkerId = redisUtils.setIfAbsent(CacheConstants.YITTER_WORKERID_MAXID_KEY, 1);
            if(!initWorkerId) {
                // 若 Key 已存在，则最大的机器号自增 1
                redisUtils.incr(CacheConstants.YITTER_WORKERID_MAXID_KEY, 1);
            }

            Integer workerId = redisUtils.get(CacheConstants.YITTER_WORKERID_MAXID_KEY);
            IdGeneratorOptions options = new IdGeneratorOptions(workerId.shortValue());
            YitIdHelper.setIdGenerator(options);

            // 设置mac地址 - workerid 到 hash结构
            redisUtils.hset(CacheConstants.YITTER_ID_IP_KEY, macAddress, workerId);

            logger.info("已配置分布式Id Work, {} - {}", macAddress, workerId);
        }finally {
            redissonUtils.unlock(CacheConstants.YITTER_ID_GENERATOR_KEY);
            logger.info("{} 配置分布式Id Work缓存========结束", macAddress);
        }
    }

    /**
     * 延迟队列侦听
     */
    private void delayQueueListen(){
        logger.info("配置 redis 延迟队列========开始");

        for (RedisDelayQueueEnum queueEnum : RedisDelayQueueEnum.values()) {
            new Thread(() -> {
                while(true){
                    try{
                        Object value = redissonUtils.getDelayQueueMsg(queueEnum.getCode());
                        if(Objects.nonNull(value)){
                            RedisDelayQueueHandler handler = SpringUtils.getBean(queueEnum.getBeanId());
                            handler.execute(value);
                        }
                    }catch (Exception e){
                        logger.error("延迟队列: {}, 启动失败: ", queueEnum.getName(), e.getMessage());
                    }
                }
            }).start();
        }

        logger.info("配置 redis 延迟队列========结束");
    }

}
