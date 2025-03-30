package com.chencj.user.config;


import com.chencj.common.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: WebConfig
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 13:26
 * @Version: 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-avatar-dir}")
    private String avatarPath;

    /**
     * 用户头像存放的静态资源目录
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:" + avatarPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .excludePathPatterns("/user/login", "/user/register");
    }
}
