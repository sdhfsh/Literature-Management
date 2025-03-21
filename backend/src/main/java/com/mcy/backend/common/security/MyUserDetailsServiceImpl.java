package com.mcy.backend.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcy.backend.common.exception.UserCountLockException;
import com.mcy.backend.entity.User;
import com.mcy.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User loginUser = userMapper.selectOne(queryWrapper);
        System.out.println("loginUser: " + loginUser);
        if (Objects.isNull(loginUser)) {
            System.out.println("loginUser"+"为空");
            throw new UsernameNotFoundException("用户名或者密码错误！");
        } else if ("1".equals(loginUser.getStatus())) {
            throw new UserCountLockException("该用户账号被封禁，具体请联系管理员");
        }
        // TODO 查询对应的权限信息

        // 把数据封装为 UserDetails 对象
        MyUserDetails loginUserDetails = new MyUserDetails(loginUser);
        return loginUserDetails;
    }
}
