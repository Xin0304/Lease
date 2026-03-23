package com.sanjin.lease.common.redisconstant;

public class RedisConstant {
    /**
     * 管理后台登录相关的 Redis 键前缀
     */
    public static final String ADMIN_LOGIN_PREFIX = "admin:login:";
    /**
     * 管理后台登录验证码的过期时间（秒）
     */
    public static final Integer ADMIN_LOGIN_CAPTCHA_TTL_SEC = 60;
    /**
     * 移动端应用登录相关的 Redis 键前缀
     */
    public static final String APP_LOGIN_PREFIX = "app:login:";
    
    /**
     * 移动端应用登录验证码重新发送的时间间隔（秒）
     */
    public static final Integer APP_LOGIN_CODE_RESEND_TIME_SEC = 60;
    /**
     * 移动端应用登录验证码的过期时间（秒）
     */
    public static final Integer APP_LOGIN_CODE_TTL_SEC = 60 * 5;
}
