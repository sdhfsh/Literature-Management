package com.mcy.backend.controller;

import cn.hutool.json.JSONUtil;
import com.mcy.backend.entity.Result;
import com.mcy.backend.service.UserService;
import com.mcy.backend.vo.LoginUserVO;
import com.mcy.backend.vo.RegisterUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginUserVO loginUserVO) {
        System.out.println(loginUserVO);

        String username = loginUserVO.getUsername();
        String password = loginUserVO.getPassword();
        return userService.login(username, password);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterUserVO registerUserVO) {
        System.out.println("用户注册信息：" + registerUserVO);
        return userService.register(registerUserVO);
    }

    @RequestMapping("/logout")
    public Result logout() {
        return userService.logout();
    }
}
