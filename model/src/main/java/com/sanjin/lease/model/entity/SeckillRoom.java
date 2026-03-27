package com.sanjin.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "秒杀房间")
@TableName(value ="seckill_room")
@Data
public class SeckillRoom extends BaseEntity{

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联真实房源 ID")
    private Long roomId;

    @Schema(description = "秒杀库存（特价名额）")
    private Integer stock;

    @Schema(description = "秒杀开始时间")
    private Date startTime;

    @Schema(description = "秒杀结束时间")
    private Date endTime;
}
