package com.sanjin.lease.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Schema(description = "秒杀订单")
@TableName(value = "seckill_order")
@Data
public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单号")
    @TableId(value = "order_no", type = IdType.ASSIGN_ID)
    private String orderNo;

    @Schema(description = "用户 ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "房源 ID")
    @TableField("room_id")
    private Long roomId;

    @Schema(description = "状态：0-占位成功待支付，1-已支付签约")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @Schema(description = "更新时间")
    @TableField("gmt_modified")
    private Date gmtModified;
}
