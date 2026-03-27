package com.sanjin.lease.web.admin.vo.seckill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author liubo
 * @description 管理端秒杀订单列表项 VO
 * @createDate 2024-03-30
 */
@Data
@Schema(description = "管理端秒杀订单列表项")
public class SeckillOrderAdminVo {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "房间 ID")
    private Long roomId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "订单状态：0-占位成功待支付，1-已支付签约")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "下单时间")
    private Date gmtCreate;

    @Schema(description = "支付时间")
    private Date paymentTime;
}
