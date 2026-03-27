package com.sanjin.lease.web.app.vo.seckill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author liubo
 * @description APP 端秒杀订单结果 VO
 * @createDate 2024-03-30
 */
@Data
@Schema(description = "APP 端秒杀订单结果")
public class SeckillOrderResultVo {

    @Schema(description = "是否抢购成功")
    private Boolean success;

    @Schema(description = "订单号（成功时返回）")
    private String orderNo;

    @Schema(description = "房间 ID")
    private Long roomId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "秒杀价格（元/月）")
    private String seckillPrice;

    @Schema(description = "支付截止时间")
    private Date paymentDeadline;

    @Schema(description = "提示信息")
    private String message;
}
