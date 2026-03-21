// 文件路径: com/sanjin/lease/common/config/Knife4jConfiguration.java
package com.sanjin.lease.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    // ========== 全局 OpenAPI 信息 ==========
    @Bean
    public OpenAPI globalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("谷粒租房平台 API 文档")
                        .version("1.0")
                        .description("包含后台管理端与用户端所有接口")
                        .termsOfService("http://doc.xiaominfo.com")
                        .license(new License().name("Apache 2.0").url("http://doc.xiaominfo.com")))
                .components(new Components()
                        // 为 App 端添加 Token 认证（Header: access_token）
                        .addSecuritySchemes("access_token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("access_token")));
    }

    // ========== 后台管理端 API 分组 ==========
    @Bean
    public GroupedOpenApi adminSystemAPI() {
        return GroupedOpenApi.builder()
                .group("系统信息管理")
                .pathsToMatch("/admin/system/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminLoginAPI() {
        return GroupedOpenApi.builder()
                .group("后台登录管理")
                .pathsToMatch("/admin/login/**", "/admin/info")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApartmentAPI() {
        return GroupedOpenApi.builder()
                .group("公寓信息管理")
                .pathsToMatch(
                        "/admin/apartment/**",
                        "/admin/room/**",
                        "/admin/label/**",
                        "/admin/facility/**",
                        "/admin/fee/**",
                        "/admin/attr/**",
                        "/admin/payment/**",
                        "/admin/region/**",
                        "/admin/term/**",
                        "/admin/file/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi adminLeaseAPI() {
        return GroupedOpenApi.builder()
                .group("租赁信息管理")
                .pathsToMatch("/admin/appointment/**", "/admin/agreement/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminUserAPI() {
        return GroupedOpenApi.builder()
                .group("平台用户管理")
                .pathsToMatch("/admin/user/**")
                .build();
    }

    // ========== 用户 App 端 API 分组 ==========
    @Bean
    public GroupedOpenApi appUserAPI() {
        return GroupedOpenApi.builder()
                .group("用户信息")
                .addOperationCustomizer((operation, handlerMethod) ->
                        operation.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("access_token")))
                .pathsToMatch("/app/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi appLoginAPI() {
        return GroupedOpenApi.builder()
                .group("登录信息")
                .addOperationCustomizer((operation, handlerMethod) ->
                        operation.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("access_token")))
                .pathsToMatch("/app/login/**", "/app/info")
                .build();
    }

    @Bean
    public GroupedOpenApi appPersonAPI() {
        return GroupedOpenApi.builder()
                .group("个人信息")
                .addOperationCustomizer((operation, handlerMethod) ->
                        operation.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("access_token")))
                .pathsToMatch("/app/history/**", "/app/appointment/**", "/app/agreement/**")
                .build();
    }

    @Bean
    public GroupedOpenApi appLookForRoomAPI() {
        return GroupedOpenApi.builder()
                .group("找房信息")
                .addOperationCustomizer((operation, handlerMethod) ->
                        operation.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("access_token")))
                .pathsToMatch("/app/apartment/**", "/app/room/**", "/app/payment/**", "/app/region/**", "/app/term/**")
                .build();
    }

    // ========== 全部接口（可选）==========
    @Bean
    public GroupedOpenApi allAPI() {
        return GroupedOpenApi.builder()
                .group("全部接口")
                .pathsToMatch("/**")
                .build();
    }
}