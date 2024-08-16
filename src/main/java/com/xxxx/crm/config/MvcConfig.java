package com.xxxx.crm.config;

import com.xxxx.crm.interceptor.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 实例化拦截器
     */
    @Bean
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 配置noLoginInterceptor拦截器的拦截策略！
        registry.addInterceptor(noLoginInterceptor())
                // 需要拦截的资源路径：项目路径下的所有内容
                .addPathPatterns("/**")
                // 不需要拦截的过滤规则：访问登录页、访问登录功能、各种静态资源目录
                .excludePathPatterns("/index", "/user/login", "/css/**", "/images/**", "/js/**", "/lib/**");
    }

}
