package com.mcy.backend.config;

import com.mcy.backend.common.security.MyUserDetailsServiceImpl;
import com.mcy.backend.common.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * spring security配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsServiceImpl myUserDetailsService;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private static final String[] URL_WHITELIST = {
            "/user/login",
            "/user/register",
//            "/login",
//            "/logout",
            "/captcha",
            "/password",
            "/image/**"
//            "/test/**"
    };

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启跨域 以及csrf攻击 关闭
        http
                .cors()
                .and()
                .csrf()
                .disable()

                // session禁用配置
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 无状态

                // 拦截规则配置
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).anonymous()
//                .antMatchers("/test/**").permitAll()// 白名单 放行
                .anyRequest().authenticated();


        // 异常处理配置

        // 自定义过滤器配置
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
