package com.sanjin.lease.web.app.controller.seckill;

import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.web.app.service.SeckillRoomService;
import com.sanjin.lease.web.app.vo.seckill.SeckillOrderDetailVo;
import com.sanjin.lease.web.app.vo.seckill.SeckillOrderResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liubo
 * @description APP 端秒杀订单控制器
 * @createDate 2024-03-30
 */
@Tag(name = "APP 端秒杀订单")
@RestController
@RequestMapping("/app/seckill/order")
public class SeckillOrderController {

    @Autowired
    private SeckillRoomService seckillRoomService;
    @Operation(summary = "创建秒杀订单")
    @PostMapping("/{roomId}")
    public Result<SeckillOrderDetailVo> createOrder(@PathVariable Long roomId) {
         // 实现秒杀订单创建逻辑（包括 Redis 预减库存、分布式锁等）
        SeckillOrderDetailVo seckillOrderDetailVo = new SeckillOrderDetailVo();
        return Result.ok(seckillOrderDetailVo);
    }

    @Operation(summary = "查询秒杀订单状态")
    @GetMapping("/{orderNo}")
    public Result<SeckillOrderResultVo> getOrderStatus(@PathVariable String orderNo) {
        // 实现订单状态查询逻辑
        SeckillOrderResultVo vo = new SeckillOrderResultVo();
        return Result.ok(vo);
    }
}
