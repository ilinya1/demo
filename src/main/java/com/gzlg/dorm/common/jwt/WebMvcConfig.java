package com.gzlg.dorm.common.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册登录鉴权拦截器 + 静态资源映射。
 * 鉴权：放行登录接口 /auth/login，其余 /api 下接口均需有效 token（context-path=/api 已配置）。
 * 静态资源：/uploads/** 落到工作区 uploads 目录（卫生照片/报修图片等现场照片，开发期由脚本写入占位样例）。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/uploads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 相对路径 file:uploads/ 基于后端启动工作目录（spring-boot:run 时为项目根目录）
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}