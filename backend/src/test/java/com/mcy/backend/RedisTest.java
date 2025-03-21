package com.mcy.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void connectTest() {
        String key = "machaoyue";
//        stringRedisTemplate.opsForValue().set(key, "123456");
//        System.out.println(stringRedisTemplate.opsForValue().get(key));
        Boolean delete = stringRedisTemplate.delete(key);
        if (delete) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }
}
