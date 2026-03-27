package com.sanjin.lease.web.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sanjin.lease.model.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liubo
 * @description 针对表【seckill_order(秒杀订单表)】的数据库操作 Mapper
 * @createDate 2024-03-30
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

}
