package com.mcy.backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web项目配置类
 * @author java1234_小锋
 * @site www.java1234.com
 * @company 南通小锋网络科技有限公司
 * @create 2022-02-24 11:45
 */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE","OPTIONS")
                .allowedHeaders("*")  // 允许的请求头
//                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/userAvatar/**")
                .addResourceLocations("file:/D:/Projects/基于共享机制的文献管理系统/Literature-Management/lmsm-front/userAvatar/");

        // 映射文献下载目录
//        registry.addResourceHandler("/file/download/**")
//                .addResourceLocations("file:/D:/Projects/基于共享机制的文献管理系统/Literature-Management/backend/documents/");
    }

}
