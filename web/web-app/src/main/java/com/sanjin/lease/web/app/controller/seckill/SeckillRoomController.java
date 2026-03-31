package com.sanjin.lease.web.app.controller.seckill;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.web.app.service.SeckillRoomService;
import com.sanjin.lease.web.app.vo.seckill.SeckillRoomItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * @author liubo
 * @description APP 端秒杀房间控制器
 * @createDate 2024-03-30
 */
@Tag(name = "APP 端秒杀房间")
@RestController
@RequestMapping("/app/seckill/room")
public class SeckillRoomController {



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
}
