package com.self.common.manager;

import com.self.common.utils.SpringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;

public class AsyncManager {

    //线程池执行器
    private static ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //定时任务调度器管理
    private static AsyncManager asyncManager;

    private AsyncManager(){
        threadPoolTaskExecutor = SpringUtils.getBean("threadPoolTaskExecutor");
    }

    public static AsyncManager getInstance(){
        if (Objects.isNull(asyncManager)){
            asyncManager = new AsyncManager();
        }

        return asyncManager;
    }

    public  void executor(Runnable runnable){
        threadPoolTaskExecutor.execute(runnable);
    }

    public void shutDown() {
        threadPoolTaskExecutor.shutdown();
    }

}
