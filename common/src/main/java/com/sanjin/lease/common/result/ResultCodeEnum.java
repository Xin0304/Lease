package com.sanjin.lease.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARAM_ERROR(202, "参数不正确"),
    SERVICE_ERROR(203, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    DELETE_ERROR(207, "请先删除子集"),

    ADMIN_ACCOUNT_EXIST_ERROR(301, "账号已存在"),
    ADMIN_CAPTCHA_CODE_ERROR(302, "验证码错误"),
    ADMIN_CAPTCHA_CODE_EXPIRED(303, "验证码已过期"),
    ADMIN_CAPTCHA_CODE_NOT_FOUND(304, "未输入验证码"),
    CAPTCHA_NOT_FOUND( 400,"验证码不存在或已过期"),
    CAPTCHA_ERROR(401, "验证码错误"),


    ADMIN_LOGIN_AUTH(305, "未登陆"),
    ADMIN_ACCOUNT_NOT_EXIST_ERROR(306, "账号不存在"),
    ADMIN_ACCOUNT_ERROR(307, "用户名或密码错误"),
    ADMIN_ACCOUNT_DISABLED_ERROR(308, "该用户已被禁用"),
    ADMIN_ACCESS_FORBIDDEN(309, "无访问权限"),

    APP_LOGIN_AUTH(501, "未登陆"),
    APP_LOGIN_PHONE_EMPTY(502, "手机号码为空"),
    APP_LOGIN_CODE_EMPTY(503, "验证码为空"),
    APP_SEND_SMS_TOO_OFTEN(504, "验证法发送过于频繁"),
    APP_LOGIN_CODE_EXPIRED(505, "验证码已过期"),
    APP_LOGIN_CODE_ERROR(506, "验证码错误"),
    APP_ACCOUNT_DISABLED_ERROR(507, "该用户已被禁用"),

    //秒杀
    SECKILL_NOT_START(2001, "秒杀活动还没开始，请耐心等待"),
    SECKILL_HAS_END(2002, "秒杀活动已经结束，下次早点来哦"),


    // 锁与并发
    SECKILL_BUSY(2003, "抢租人数过多，请稍后再试"), // 对应拿不到分布式锁的情况
    SECKILL_ROOM_NOT_EXIST(2000, "房源不存在"),
    // 库存与订单
    SECKILL_STOCK_NOT_ENOUGH(2004, "手慢了，特价房已被抢光"),
    SECKILL_REPEAT(2005, "您已经秒杀过了，不要重复下单"),

    // 安全校验
    SECKILL_URL_ERROR(2006, "秒杀地址校验失败，非法请求"),

    SECKILL_SUCCESS(2000, "恭喜！抢房成功，请尽快支付"),

    TOKEN_EXPIRED(601, "token过期"),
    TOKEN_INVALID(602, "token非法");




    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
