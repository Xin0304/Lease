package com.sanjin.lease.web.admin.controller.lease;


import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.entity.ViewAppointment;
import com.sanjin.lease.model.enums.AppointmentStatus;
import com.sanjin.lease.web.admin.service.ViewAppointmentService;
import com.sanjin.lease.web.admin.vo.appointment.AppointmentQueryVo;
import com.sanjin.lease.web.admin.vo.appointment.AppointmentVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "预约看房管理")
@RequestMapping("/admin/appointment")
@RestController
public class ViewAppointmentController {

    @Autowired
    private ViewAppointmentService viewAppointmentService;

    @Operation(summary = "分页查询预约信息")
    @GetMapping("page")
    public Result<IPage<AppointmentVo>> page(@RequestParam long current,
                                             @RequestParam long size,
                                             AppointmentQueryVo queryVo) {
        IPage<AppointmentVo> page = new Page<>(current, size);
        IPage<AppointmentVo> appointmentVoIPage = viewAppointmentService.pageAppointmentByQuery(page, queryVo);
        return Result.ok(appointmentVoIPage);
    }

    @Operation(summary = "根据id更新预约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id,
                                   @RequestParam AppointmentStatus status) {
        LambdaUpdateWrapper<ViewAppointment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ViewAppointment::getApartmentId,id);
        updateWrapper.set(ViewAppointment::getAppointmentStatus, status);
        viewAppointmentService.update(updateWrapper);
        return Result.ok();
    }

}
