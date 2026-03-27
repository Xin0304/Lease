package com.sanjin.lease.web.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "秒杀请求参数")
public class SeckillReqDTO {

    @NotNull(message = "房间 ID 不能为空")
    @Schema(description = "房间 ID")
    private Long roomId;

    @NotNull(message = "用户 ID 不能为空")
    @Schema(description = "用户 ID")
    private Long userId;
}
