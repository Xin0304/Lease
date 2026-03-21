package com.sanjin.lease.web.app.controller.appointment;


import com.sanjin.lease.common.context.LoginUserContext;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.entity.ViewAppointment;
import com.sanjin.lease.web.app.service.ViewAppointmentService;
import com.sanjin.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.sanjin.lease.web.app.vo.appointment.AppointmentItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "看房预约信息")
@RestController("appViewController")
@RequestMapping("/app/appointment")
public class ViewAppointmentController {


    @Autowired
    private ViewAppointmentService ViewAppointmentService;
    @Operation(summary = "保存或更新看房预约")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ViewAppointment viewAppointment) {
        viewAppointment.setUserId(LoginUserContext.getLoginUser().getUserId());
        ViewAppointmentService.saveOrUpdate(viewAppointment);
        return Result.ok();
    }

    @Operation(summary = "查询个人预约看房列表")
    @GetMapping("listItem")
    public Result<List<AppointmentItemVo>> listItem() {
        List<AppointmentItemVo> list = ViewAppointmentService.listAppointmentItemByUserId(LoginUserContext.getLoginUser().getUserId());
        return Result.ok(list);
    }


    @GetMapping("getDetailById")
    @Operation(summary = "根据ID查询预约详情信息")
    public Result<AppointmentDetailVo> getDetailById(Long id) {
        AppointmentDetailVo detail = ViewAppointmentService.getAppointmentDetailVoById(id);
        return Result.ok(detail);
    }

}

