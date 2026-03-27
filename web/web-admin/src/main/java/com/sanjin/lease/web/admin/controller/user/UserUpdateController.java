package com.sanjin.lease.web.admin.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user/update")
public class UserUpdateController {

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public String updatePassword(Long id) {

        return "修改密码成功";
    }
}
