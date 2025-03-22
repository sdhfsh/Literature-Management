package com.mcy.backend.controller;

import com.mcy.backend.entity.Result;
import com.mcy.backend.entity.User;
import com.mcy.backend.service.UserService;
import com.mcy.backend.utils.JwtUtils;
import com.mcy.backend.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ROLE_admin')")
    @GetMapping("/user/{id}")
    public Result getUser(@PathVariable("id") Integer id, @RequestHeader(required = false) String token) {
        if (StringUtil.isNotEmpty(token)) {
            User user = userService.getById(id);
            return Result.ok().put("user", user);
        } else {
            return Result.error(401, "没有权限访问");
        }
    }

    @RequestMapping("/login")
    public Result login() {
        String token = JwtUtils.genJwtToken("java1234");
        System.out.println(token);
        return Result.ok().put("token", token);
    }
}
