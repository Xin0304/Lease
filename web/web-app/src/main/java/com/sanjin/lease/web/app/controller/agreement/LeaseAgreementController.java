package com.sanjin.lease.web.app.controller.agreement;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sanjin.lease.common.context.LoginUser;
import com.sanjin.lease.common.context.LoginUserContext;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.entity.LeaseAgreement;
import com.sanjin.lease.model.enums.LeaseStatus;
import com.sanjin.lease.web.app.service.LeaseAgreementService;
import com.sanjin.lease.web.app.vo.agreement.AgreementDetailVo;
import com.sanjin.lease.web.app.vo.agreement.AgreementItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginContext;
import java.util.List;

@RestController("appLeaseAgreenmentController")
@RequestMapping("/app/agreement")
@Tag(name = "租约信息")
public class LeaseAgreementController {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Operation(summary = "获取个人租约基本信息列表")
    @GetMapping("listItem")
    public Result<List<AgreementItemVo>> listItem() {
        List<AgreementItemVo> agreementItemVoList =
                            leaseAgreementService.getAgreementVoListByPhone(LoginUserContext.getLoginUser().getUsername());

        return Result.ok(agreementItemVoList);
    }

    @Operation(summary = "根据id获取租约详细信息")
    @GetMapping("getDetailById")
    public Result<AgreementDetailVo> getDetailById(@RequestParam Long id) {
        AgreementDetailVo agreementItemVo =
                leaseAgreementService.getAgreementVoListById(id);
        return Result.ok(agreementItemVo);
    }

    @Operation(summary = "根据id更新租约状态", description = "用于确认租约和提前退租")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id,
                                   @RequestParam LeaseStatus leaseStatus) {
        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseAgreement::getId, id);
        updateWrapper.set(LeaseAgreement::getStatus, leaseStatus);
        leaseAgreementService.update(updateWrapper);
        return Result.ok(leaseAgreementService);
    }

    @Operation(summary = "保存或更新租约", description = "用于续约")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody LeaseAgreement leaseAgreement) {
        return Result.ok(leaseAgreementService.saveOrUpdate(leaseAgreement));
    }
}