package com.sanjin.lease.web.app.controller.seckill;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.dto.SeckillReqDTO;
import com.sanjin.lease.web.app.service.SeckillRoomService;
import com.sanjin.lease.web.app.vo.seckill.SeckillRoomItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author liubo
 *  APP 端秒杀房间控制器
 *  2024-03-30
 */
@Tag(name = "APP 端秒杀房间")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/app/seckill/room")
public class SeckillRoomController {

    private final SeckillRoomService seckillRoomService;

    @Operation(summary = "分页查询正在进行中的秒杀房间列表")
    @GetMapping("/page")
    public Result<Page<SeckillRoomItemVo>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        // 实现分页查询逻辑，只查询进行中的秒杀活动
        Page<SeckillRoomItemVo> page = new Page<>(current, size);
        return Result.ok(page);
    }

    @Operation(summary = "根据 ID 查询秒杀房间详情")
    @GetMapping("/{id}")
    public Result<SeckillRoomItemVo> getById(@PathVariable Long id) {
        //实现查询详情逻辑
        SeckillRoomItemVo vo = new SeckillRoomItemVo();
        return Result.ok(vo);
    }

    @Operation(summary = "秒杀")
    @PostMapping("/{id}/seckill")
    public Result<String> thouserexecuteSeckill(SeckillReqDTO reqDTO,HttpServletRequest request // 获取请求方IP，可用于日志记录或简单限流
    ) {
        try {
            System.out.println("收到秒杀请求，来自IP: " + request.getRemoteAddr());
            String order = seckillRoomService.executeSeckill(reqDTO);
            if (order != null) {
                // 抢购成功
                return Result.ok(order);
            } else {
                // 抢购失败，可能是没抢到或者库存不足
                return Result.fail(409, "很遗憾，手慢一步，商品已售罄！");
            }
        } catch (Exception e) {
            // 任何非预期异常，都返回失败
            System.err.println("秒杀接口发生异常: " + e.getMessage());
            log.error("秒杀接口发生异常", e);
            return Result.fail(202,"系统繁忙，请稍后再试");
        }
    }
}