package com.sanjin.lease.web.admin.controller.apartment;


import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.entity.PaymentType;
import com.sanjin.lease.web.admin.service.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "支付方式管理")
@RequestMapping("/admin/payment")
@RestController
public class PaymentTypeController {

    @Autowired
    private PaymentTypeService paymentTypeService;

    @Operation(summary = "查询全部支付方式列表")
    @GetMapping("list")
    public Result listPaymentType(){
        return Result.ok(paymentTypeService.list());
    }

    @Operation(summary = "根据ID查询支付方式")
    @GetMapping("getlistPaymentTypeById/{id}")
    public Result getlistPaymentTypeById(@PathVariable Long id){
        return Result.ok(paymentTypeService.getById(id));
    }

    @Operation(summary = "保存或更新支付方式")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody PaymentType paymentType){
        boolean isSuccess = paymentTypeService.saveOrUpdate(paymentType);
        return isSuccess?Result.ok():Result.fail();
    }

    @Operation(summary = "根据ID删除支付方式")
    @DeleteMapping("deleteById/")
    public Result deleteById(@RequestParam Long id){
        boolean b = paymentTypeService.removeById(id);
        return b?Result.ok():Result.fail();
    }
}















