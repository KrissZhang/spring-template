package com.self.web;

import com.google.common.collect.Lists;
import com.self.common.utils.RedissonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebTest {

    @Autowired
    private RedissonUtils redissonUtils;

    private static Integer count = 0;

    @Test
    public void test(){

    }

    @Test
    public void testRedisson(){
        List<Integer> list = Collections.synchronizedList(Lists.newArrayList());
        for (int i = 1; i <= 100; i++) {
            list.add(i);
        }

        list.parallelStream().forEach(integer -> {
            RLock rLock = redissonUtils.lock("lock:prefix:" + "redisson");
            try {
                //业务逻辑
                count += integer;
                System.out.println("count:" + count);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }finally {
                redissonUtils.unlockAviable(rLock);
            }
        });
    }

}
