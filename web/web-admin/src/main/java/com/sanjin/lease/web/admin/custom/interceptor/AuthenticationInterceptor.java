package com.sanjin.lease.web.admin.custom.interceptor;

import com.sanjin.lease.common.context.LoginUser;
import com.sanjin.lease.common.context.LoginUserContext;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.common.result.ResultCodeEnum;
import com.sanjin.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String token = request.getHeader("access-token");

        //2 判断token是否为空，如果空，提示用户
        if(token == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        } else { //3 如果不为空，解析token
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            LoginUserContext.setLoginUser(new LoginUser(userId, username));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        LoginUserContext.clear();
    }
}
