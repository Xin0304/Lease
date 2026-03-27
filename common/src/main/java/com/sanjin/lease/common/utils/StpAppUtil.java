package com.sanjin.lease.common.utils;

import cn.dev33.satoken.stp.StpLogic;
import com.sanjin.lease.common.account.AccountType;

/**
 * App端租客 权限认证工具类
 * 仿照 StpUtil 编写，但 loginType 设为 "app"
 */
public class StpAppUtil {
    // 1.创建类

    // 2. 底层逻辑对象，所有的操作最终都委托给这个 stpLogic
    // 关键点：构造参数传入上面的 TYPE
    public static StpLogic stpLogic = new StpLogic(AccountType.APP);

    // 3. 静态代理常用方法（用到哪个写哪个）

    // 会话登录
    public static void login(Object id) {
        stpLogic.login(id);
    }

    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }
    // 获取当前会话账号id
    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    // 获取当前会话账号id (转long)
    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    // 校验当前会话是否已登录，如未登录，则抛出 `NotLoginException` 异常
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    // 当前会话注销登录
    public static void logout() {
        stpLogic.logout();
    }

    // 查询当前会话是否登录
    public static boolean isLogin() {
        return stpLogic.isLogin();
    }
}
