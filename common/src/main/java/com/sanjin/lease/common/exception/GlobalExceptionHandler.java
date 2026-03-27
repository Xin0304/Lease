package com.sanjin.lease.common.exception;


import cn.dev33.satoken.exception.NotLoginException;
import com.sanjin.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }


    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result error(LeaseException e) {
        e.printStackTrace();
        Integer code = e.getCode();
        String message = e.getMessage();
        return Result.fail(code, message);
    }

    /**
     * 专门处理 Sa-Token 的登录认证异常
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public Result error(NotLoginException nle) {
        // 打印堆栈（可选）
        nle.printStackTrace();
        // 根据不同场景返回不同提示
        String message = "当前会话未登录";
        if (nle.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "您的账号已在其他设备登录，您已被迫下线";
        } else if (nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "登录已过期，请重新登录";
        }

        // 返回统一的 Result 对象，状态码通常定为 401
        return Result.fail(401, message);
    }

}
