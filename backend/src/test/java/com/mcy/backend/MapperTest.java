package com.mcy.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcy.backend.entity.User;
import com.mcy.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testBCryptPasswordEncoder() {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, 1);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePwd = bCryptPasswordEncoder.encode("123456");
        System.out.println(encodePwd);
        boolean matches = bCryptPasswordEncoder.matches(user.getPassword(), encodePwd);
        System.out.println(matches);
    }
}
