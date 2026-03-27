package com.sanjin.lease.web.app.service;

import com.sanjin.lease.web.app.vo.user.LoginVo;
import com.sanjin.lease.web.app.vo.user.UserInfoVo;

import java.io.IOException;

public interface LoginService {


    void getCode(String phone) throws IOException;


    String userLogin(LoginVo loginVo);

    UserInfoVo getUserInfoId(Long userId);

    /**
     * 根据用户ID查询手机号（租约/历史等表通常用 phone 做字段查询）
     */
    String getUserPhone(Long userId);

}
