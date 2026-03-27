package com.sanjin.lease.web.app.vo.seckill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liubo
 * @description APP 端秒杀房间详情 VO
 * @createDate 2024-03-30
 */
@Data
@Schema(description = "APP 端秒杀房间详情")
public class SeckillRoomDetailVo {

    @Schema(description = "秒杀房源表 ID")
    private Long id;

    @Schema(description = "关联真实房源 ID")
    private Long roomId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "房间原价（元/月）")
    private BigDecimal rent;

    @Schema(description = "秒杀价格（元/月）")
    private BigDecimal seckillPrice;

    @Schema(description = "秒杀库存（特价名额）")
    private Integer totalStock;

    @Schema(description = "剩余库存")
    private Integer remainStock;

    @Schema(description = "秒杀开始时间")
    private Date startTime;

    @Schema(description = "秒杀结束时间")
    private Date endTime;

    @Schema(description = "秒杀状态：0-未开始，1-进行中，2-已结束")
    private Integer status;

    @Schema(description = "倒计时（秒），用于前端显示")
    private Long countdown;

    // ========== 公寓信息 ==========
    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "公寓地址")
    private String apartmentAddress;

    @Schema(description = "公寓图片列表")
    private String[] apartmentImages;

    // ========== 房间信息 ==========
    @Schema(description = "房间面积（平方米）")
    private Double area;

    @Schema(description = "房间楼层")
    private Integer floor;

    @Schema(description = "房间朝向")
    private String direction;

    @Schema(description = "房间配套描述")
    private String facilityDescription;
}
