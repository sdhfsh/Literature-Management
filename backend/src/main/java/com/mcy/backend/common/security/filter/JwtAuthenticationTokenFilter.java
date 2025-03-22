package com.mcy.backend.common.security.filter;

import com.mcy.backend.common.constant.JwtConstant;
import com.mcy.backend.common.security.MyUserDetails;
import com.mcy.backend.entity.CheckResult;
import com.mcy.backend.mapper.UserMapper;
import com.mcy.backend.utils.JwtUtils;
import com.mcy.backend.utils.RedisCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCache redisCache;

    private static final String[] URL_WHITELIST = {
            "/user/login",
            "/login",
            "/logout",
            "/captcha",
            "/password",
            "/image/**"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 获取 token
        String token = request.getHeader("token");
        String requestURI = request.getRequestURI();
        System.out.println("请求uri：" + requestURI);
        // 如果token为空，或者uri在白名单里，
        if (!StringUtils.hasText(token) || new ArrayList<String>(Arrays.asList(URL_WHITELIST)).contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        CheckResult checkResult = JwtUtils.validateJWT(token);
        if (!checkResult.isSuccess()) {
            switch (checkResult.getErrCode()) {
                case JwtConstant.JWT_ERRCODE_NULL:throw new JwtException("Token不存在");
                case JwtConstant.JWT_ERRCODE_FAIL:throw new JwtException("Token验证不通过");
                case JwtConstant.JWT_ERRCODE_EXPIRE:throw new JwtException("Token过期");
            }
        }
        // 解析 token中的username
        Claims claims = JwtUtils.parseJWT(token);  // 获取主体信息
        String username = claims.getSubject();     // subject就是用户名

        MyUserDetails loginUser = redisCache.getCacheObject("login:" + username);
        if (Objects.isNull(loginUser)) {
            throw new JwtException("用户未登录");
        }

        // 存入SecurityHolder
        // TODO 获取权限信息
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), loginUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}
