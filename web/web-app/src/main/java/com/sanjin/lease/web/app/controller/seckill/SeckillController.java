package com.sanjin.lease.web.app.controller.seckill;

import com.sanjin.lease.common.result.Result;
import com.sanjin.lease.model.dto.SeckillReqDTO;
import com.sanjin.lease.web.app.service.SeckillRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "秒杀房源接口")
@RestController
@RequestMapping("/app/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillRoomService seckillRoomService;

    @Operation(summary = "提交秒杀请求")
    @PostMapping("/execute")
    public Result<String> executeSeckill(@RequestBody SeckillReqDTO reqDTO) {
        // 直接调用你刚刚重构好的 Service 方法
        String result = seckillRoomService.executeSeckill(reqDTO);

        // 根据返回的字符串判断是成功还是失败（这里逻辑可以根据你的 ResultCodeEnum 优化）
        if ("成功".equals(result)) {
            return Result.ok(result);
        } else {
            return Result.fail(202, "秒杀失败：" +result);
        }
    }
}
