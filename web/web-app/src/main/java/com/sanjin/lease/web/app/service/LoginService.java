package com.sanjin.lease.web.app.service;

import com.sanjin.lease.web.app.vo.user.LoginVo;
import com.sanjin.lease.web.app.vo.user.UserInfoVo;

import java.io.IOException;

public interface LoginService {


    void getCode(String phone) throws IOException;

    String login(LoginVo loginVo);

    UserInfoVo findLoginUserInfo(Long userId);
}
