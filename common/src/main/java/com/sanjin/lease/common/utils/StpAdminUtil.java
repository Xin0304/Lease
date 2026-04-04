package com.sanjin.lease.common.utils;

import cn.dev33.satoken.stp.StpLogic;
import com.sanjin.lease.common.account.AccountType;

/**
 * Admin 端 权限认证工具类（仿照 StpAppUtil）
 * <p>
 * 关键点：这里把 loginType 显式区分为 ADMIN，避免把第二个参数当 device 用错。
 */
public class StpAdminUtil {

    /**
     * 底层逻辑对象：loginType = "admin"
     */
    public static final StpLogic stpLogic = new StpLogic(AccountType.ADMIN);

    /**
     * 会话登录
     */
    public static void login(Object id) {
        stpLogic.login(id);
    }

    /**
     * 获取当前会话账号id（转 long）
     */
    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    /**
     * 校验当前会话是否已登录，如未登录则抛出 NotLoginException
     */
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    /**
     * 获取 token 字符串
     */
    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    /**
     * 踢人下线
     */
    public static void kickOut(Object loginId) {
        stpLogic.kickout(loginId);
    }

    /**
     * 退出登录
     */
    public static void logout() {
        stpLogic.logout();
    }


}

