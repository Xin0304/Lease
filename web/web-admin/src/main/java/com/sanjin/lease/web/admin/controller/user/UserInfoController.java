package com.sanjin.lease.web.admin.controller.user;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.entity.UserInfo;
import com.sanjin.lease.model.enums.BaseStatus;
import com.sanjin.lease.web.admin.service.UserInfoService;
import com.sanjin.lease.web.admin.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户信息管理")
@RestController
@RequestMapping("/admin/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "分页查询用户信息")
    @GetMapping("page")
    public Result<IPage<UserInfo>> pageUserInfo(@RequestParam long current,
                                                @RequestParam long size,
                                                UserInfoQueryVo queryVo) {
        Page<UserInfo> page = new Page<>(current, size);
        IPage<UserInfo> pageModel = userInfoService.selectUserInfoPage(page, queryVo);

        return Result.ok(pageModel);
    }

    @Operation(summary = "根据用户id更新账号状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id,
                                   @RequestParam BaseStatus status) {
        LambdaUpdateWrapper<UserInfo> userUpdateWrapper =
                new LambdaUpdateWrapper<>();
        userUpdateWrapper.eq(UserInfo::getId, id);
        userUpdateWrapper.set(UserInfo::getStatus, status);
        userInfoService.update(userUpdateWrapper);
        return Result.ok();
    }
}
