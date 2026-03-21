package com.sanjin.lease.web.admin.service;

import com.sanjin.lease.web.admin.vo.login.CaptchaVo;
import com.sanjin.lease.web.admin.vo.login.LoginVo;
import com.sanjin.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();


    String login(LoginVo loginVo);

    SystemUserInfoVo findSystemUserInfo(Long id);
}
