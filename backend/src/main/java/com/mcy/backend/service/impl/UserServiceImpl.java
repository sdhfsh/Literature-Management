package com.mcy.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcy.backend.entity.User;
import com.mcy.backend.service.UserService;
import com.mcy.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 30679
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-03-17 15:12:52
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




