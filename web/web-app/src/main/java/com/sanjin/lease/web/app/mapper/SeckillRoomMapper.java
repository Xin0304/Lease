package com.sanjin.lease.web.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sanjin.lease.model.entity.SeckillRoom;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liubo
 * @description 针对表【seckill_room(秒杀房源表)】的数据库操作 Mapper
 * @createDate 2024-03-30
 */
@Mapper
public interface SeckillRoomMapper extends BaseMapper<SeckillRoom> {

    int decreaseStock(@NotNull(message = "房间 ID 不能为空") Long roomId);
}
