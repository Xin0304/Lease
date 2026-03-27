package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.app.SeckillEnum;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.common.result.ResultCodeEnum;
import com.sanjin.lease.model.entity.SeckillOrder;
import com.sanjin.lease.model.entity.SeckillRoom;
import com.sanjin.lease.web.app.dto.SeckillReqDTO;
import com.sanjin.lease.web.app.mapper.SeckillOrderMapper;
import com.sanjin.lease.web.app.mapper.SeckillRoomMapper;
import com.sanjin.lease.web.app.service.SeckillRoomService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillRoomImpl extends ServiceImpl<SeckillRoomMapper, SeckillRoom>
        implements SeckillRoomService {

    @Autowired
    private SeckillRoomMapper seckillRoomMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedissonClient redissonClient;

    public String executeSeckill(SeckillReqDTO req) {
        // 1. 校验活动时间（有没有开始，有没有结束）
        //1.1获取当前时间
        long now = System.currentTimeMillis();
        SeckillRoom seckillRoom = seckillRoomMapper.selectById(req.getRoomId());
        //1.2 校验活动时间
        if (seckillRoom.getStartTime().getTime() > now) {
            log.info("活动未开始");
            return ResultCodeEnum.SECKILL_NOT_START.getMessage();
        }
        if (seckillRoom.getEndTime().getTime() < now) {
            log.info("活动已结束");
            return ResultCodeEnum.SECKILL_HAS_END.getMessage();
        }
        // 2. 尝试获取 Redisson 分布式锁
        //2.1获取锁 key 是 "seckill:room:" + req.getRoomId()
        RLock lock = redissonClient.getLock(SeckillEnum.SECKILL_ROOM_ID + req.getRoomId());
        boolean islock = false;
        SeckillOrder seckillOrder = new SeckillOrder();
        // 3. 如果拿不到锁，直接抛出异常："抢租人数过多，请稍后再试"
        try {
            islock = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (!islock) {
                log.info("抢租人数过多，请稍后再试");
                return ResultCodeEnum.SECKILL_BUSY.getMessage();
            }
            // 4. 如果拿到了锁：
            //    4.1 查询数据库，看 stock 是不是 > 0
            //    4.2 如果没库存，释放锁，抛出异常："手慢了，特价房已被抢光"
            if (seckillRoom.getStock() <= 0) {
                log.info("手慢了，特价房已被抢光");
                return ResultCodeEnum.SECKILL_STOCK_NOT_ENOUGH.getMessage();
            }
            //    4.3 如果有库存，执行刚刚写的原子扣减 SQL：UPDATE stock = stock - 1 WHERE stock > 0
            int count = seckillRoomMapper.decreaseStock(req.getRoomId());
            if (count > 0) {
                log.info("抢购成功");
                //    4.4 如果 SQL 返回影响行数 > 0，说明扣减成功，插入 seckill_order 订单表
                String orderNo = now + "" + req.getUserId();
                seckillOrder.setOrderNo(orderNo);
                seckillOrder.setRoomId(req.getRoomId());
                seckillOrder.setUserId(req.getUserId());
                seckillOrder.setStatus(0);
                seckillOrder.setGmtCreate(new Date());
                seckillOrder.setGmtModified(new Date());
                seckillOrderMapper.insert(seckillOrder);
                return ResultCodeEnum.SECKILL_SUCCESS.getMessage();
            } else {
                log.info("手慢了，特价房已被抢光");
                return ResultCodeEnum.SECKILL_STOCK_NOT_ENOUGH.getMessage();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("秒杀异常", e);
            throw new LeaseException("服务器异常，请稍后再试", 202);
        } finally {
            // 5. 释放分布式锁
            if (islock){
                lock.unlock();
            }
        }
    }
}
