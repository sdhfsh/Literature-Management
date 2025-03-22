package com.mcy.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcy.backend.entity.Role;
import com.mcy.backend.entity.User;
import com.mcy.backend.mapper.RoleMapper;
import com.mcy.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

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

    @Test
    void testRoleMapper() {
        LambdaQueryWrapper<Role> roleLQW = new LambdaQueryWrapper<>();
        roleLQW.inSql(Role::getId, "SELECT role_id\n" +
                "\tFROM user_role\n" +
                "\tWHERE user_id = " + 1);
        List<Role> roles = roleMapper.selectList(roleLQW);
        for (Role role : roles) {
            System.out.println(role);
        }
    }
}
