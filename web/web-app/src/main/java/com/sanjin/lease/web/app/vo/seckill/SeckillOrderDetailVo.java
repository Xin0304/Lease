package com.sanjin.lease.web.app.vo.seckill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author liubo
 * @description APP 端秒杀订单详情 VO
 * @createDate 2024-03-30
 */
@Data
@Schema(description = "APP 端秒杀订单详情")
public class SeckillOrderDetailVo {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "房间 ID")
    private Long roomId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "订单状态：0-占位成功待支付，1-已支付签约")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "秒杀价格（元/月）")
    private String seckillPrice;

    @Schema(description = "创建时间")
    private Date gmtCreate;

    @Schema(description = "支付截止时间")
    private Date paymentDeadline;

    @Schema(description = "剩余支付时间（秒），用于倒计时")
    private Long remainPaymentTime;
}
