package com.sanjin.lease;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "com.sanjin.lease.common",   // 公共模块
        "com.sanjin.lease.model",
        "com.sanjin.lease.web.app"  // 👈 只扫描 app 前台，不扫 admin！

})
@MapperScan("com.sanjin.lease.web.app.mapper")
public class AppWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppWebApplication.class, args);
    }
}
