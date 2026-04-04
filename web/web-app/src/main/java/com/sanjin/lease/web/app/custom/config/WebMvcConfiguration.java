package com.sanjin.lease.web.app.custom.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.sanjin.lease.common.utils.StpAppUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("appWebMvcConfiguration")
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> StpAppUtil.checkLogin()))
                .addPathPatterns("/app/**")
                .excludePathPatterns("/app/login/**");

    }

}
