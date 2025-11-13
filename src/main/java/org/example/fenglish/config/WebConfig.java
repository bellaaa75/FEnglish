package org.example.fenglish.config;


import org.example.fenglish.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login", "/api/admin/login",
                        "/api/user/register", "/api/admin/register");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 增强跨域配置
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 使用allowedOriginPatterns代替allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")  // 允许所有Header
                .exposedHeaders("Authorization")  // 暴露Authorization头
                .allowCredentials(true)
                .maxAge(3600);
    }
}
