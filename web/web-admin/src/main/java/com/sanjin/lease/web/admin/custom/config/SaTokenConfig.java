package com.sanjin.lease.web.admin.custom.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.sanjin.lease.common.utils.StpAdminUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpAdminUtil.checkLogin()))
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login/**");
    }

}
