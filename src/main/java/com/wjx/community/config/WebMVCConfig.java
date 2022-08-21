package com.wjx.community.config;

import com.wjx.community.aop.LoginInterceptorHandler;
import com.wjx.community.aop.LoginRequiredInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author wjx
 * @description 拦截器配置
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptorHandler loginInterceptorHandler;
    @Resource
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptorHandler)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.html");
    }
}
