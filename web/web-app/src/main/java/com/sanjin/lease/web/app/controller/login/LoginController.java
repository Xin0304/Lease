package com.sanjin.lease.web.app.controller.login;

import com.sanjin.lease.common.context.LoginUser;
import com.sanjin.lease.common.context.LoginUserContext;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.web.app.service.LoginService;
import com.sanjin.lease.web.app.service.UserInfoService;
import com.sanjin.lease.web.app.vo.user.LoginVo;
import com.sanjin.lease.web.app.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginContext;
import java.io.IOException;

@Slf4j
@RestController("appLoginController")
@Tag(name = "登录管理")
@RequestMapping("/app/")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserInfoService userInfoService;


    @GetMapping("login/getCode")
    @Operation(summary = "获取短信验证码")
    public Result getCode(@RequestParam String phone) throws IOException {
        loginService.getCode( phone);
        return Result.ok();
    }

    @PostMapping("login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String token = loginService.userLogin(loginVo);
        return Result.ok(token);
    }

    @GetMapping("info")
    @Operation(summary = "获取登录用户信息")
    public Result<UserInfoVo> info() {
        UserInfoVo userInfoVo = loginService.getUserInfoId(LoginUserContext.getLoginUser().getUserId());
        return Result.ok(userInfoVo);
    }
}
