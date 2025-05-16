package com.mcy.backend.common.exception;

import com.mcy.backend.entity.Result;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = Exception.class)
    public Result handler(Exception e) {
        log.error("运行时异常----------{}", e.getMessage());
        if (e.getMessage().equals("Bad credentials")) {
            return Result.error("用户名或密码错误");
        }
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public Result handleJwtException(JwtException e) {
        log.error("JWT异常----------{}", e.getMessage());
        return Result.error(401, e.getMessage());
    }
}
