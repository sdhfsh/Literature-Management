package com.mcy.backend.service;

import com.mcy.backend.entity.Result;
import com.mcy.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mcy.backend.vo.RegisterUserVO;

/**
 * @author 30679
 * @description 针对表【user】的数据库操作Service
 * @createDate 2025-03-17 15:12:52
 */
public interface UserService extends IService<User> {

    Result login(String username, String password);

    Result register(RegisterUserVO registerUserVO);

    Result logout();
}
