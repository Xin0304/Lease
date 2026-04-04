package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.app.SeckillEnum;
import com.sanjin.lease.common.aspect.Log;
import com.sanjin.lease.common.exception.LeaseException;
import com.sanjin.lease.common.result.ResultCodeEnum;
import com.sanjin.lease.model.entity.SeckillOrder;
import com.sanjin.lease.model.entity.SeckillRoom;
import com.sanjin.lease.model.dto.SeckillReqDTO;
import com.sanjin.lease.web.app.mapper.SeckillOrderMapper;
import com.sanjin.lease.web.app.mapper.SeckillRoomMapper;
import com.sanjin.lease.web.app.service.SeckillRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeckillRoomImpl extends ServiceImpl<SeckillRoomMapper, SeckillRoom>
        implements SeckillRoomService {


    private final SeckillRoomMapper seckillRoomMapper;
    private final SeckillOrderMapper seckillOrderMapper;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    /**
     * 执行秒杀抢租业务
     * 实现流程：时间校验 -> 获取分布式锁 -> 库存检查 -> 扣减库存 -> 创建订单
     *
     * @param req 秒杀请求参数，包含房间 ID 和用户 ID
     * @return 秒杀结果消息：成功、失败及具体原因
     */
    @Log("特价房秒杀抢订")
    public String executeSeckill(SeckillReqDTO req) {

        // 构造 Redis Lua 脚本所需的键列表，包含库存键、开始时间键和结束时间键
        List<Object> keys = Arrays.asList(
                "seckill:stock:" + req.getRoomId(),
                "seckill:start:" + req.getRoomId(),
                "seckill:end:" + req.getRoomId()
        );

        // 执行 Redis Lua 脚本进行秒杀预检：时间校验和库存预扣减
        // 参数说明：
        //   - RScript.Mode.READ_WRITE: 脚本执行模式为读写模式
        //   - SECKILL_LUA_SCRIPT: 预先定义的 Lua 脚本
        //   - RScript.ReturnType.INTEGER: 返回类型为整数
        //   - keys: Redis 键列表
        //   - System.currentTimeMillis(): 当前系统时间（毫秒）
        //   - 1: 请求扣减的库存数量
        Long result = redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,
                SECKILL_LUA_SCRIPT,
                RScript.ReturnType.INTEGER,
                keys,

                String.valueOf(System.currentTimeMillis()),
                "1"
        );
        if (result == 1) {
            //2.1获取锁 key 是 "seckill:room:" + req.getRoomId()
            // 准备分布式锁
            RLock lock = redissonClient.getLock(SeckillEnum.SECKILL_ROOM_ID + req.getRoomId());
            // 3. 如果拿不到锁，直接抛出异常："抢租人数过多，请稍后再试"
            try {
                // 2. 尝试获取 Redisson 分布式锁
                // 尝试获取锁：等待0秒（Fast Fail，防雪崩），不指定leaseTime（启动看门狗自动续期）
                boolean isLock = lock.tryLock(3000, -1, TimeUnit.MILLISECONDS);
                if (!isLock) {
                    return ResultCodeEnum.SECKILL_BUSY.getMessage();
                }
                // 4. 如果拿到了锁：
                return transactionTemplate.execute(status -> {
                    //    4.1 查询数据库，看 stock 是不是 >0 ,
                    //    二次校验库存（非常关键，此时在锁内，数据是最新的）
                    try {
                        SeckillRoom seckillRoomLock = seckillRoomMapper.selectById(req.getRoomId());
                        //    4.2 如果没库存，释放锁，抛出异常："手慢了，特价房已被抢光"
                        if (seckillRoomLock.getStock() <= 0) {
                            return ResultCodeEnum.SECKILL_STOCK_NOT_ENOUGH.getMessage();
                        }
                        //    4.3 如果没有库存，返回 "手慢了，特价房已被抢光"
                        int count = seckillRoomMapper.decreaseStock(req.getRoomId());
                        if (count == 0) {
                            return ResultCodeEnum.SECKILL_STOCK_NOT_ENOUGH.getMessage();
                        }
                        //    4.4 如果有库存，执行刚刚写的原子扣减 SQL：UPDATE stock = stock - 1 WHERE stock > 0
                        SeckillOrder seckillOrder = new SeckillOrder();
                        // 这里暂用 时间戳+用户ID。生产环境建议用雪花算法或专门的订单号生成服务
                        String orderNo = System.currentTimeMillis() + "" + req.getUserId();
                        seckillOrder.setOrderNo(orderNo);
                        seckillOrder.setRoomId(req.getRoomId());
                        seckillOrder.setUserId(req.getUserId());
                        seckillOrder.setStatus(0);
                        // 时间交给实体类的自动填充或数据库默认值处理更佳，这里手动set也可
                        seckillOrderMapper.insert(seckillOrder);
                        return ResultCodeEnum.SECKILL_SUCCESS.getMessage();
                    } catch (DuplicateKeyException e) {
                        status.setRollbackOnly();
                        return "您已抢购过该房源，请勿重复操作";
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        throw new LeaseException("系统开小差了，请重试", 500);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("获取锁异常", e);
                throw new LeaseException("服务异常", 500);
            } finally {
                // 5. 安全释放锁：必须判断是否是当前线程持有的锁
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else if (result == -1) {
            return ResultCodeEnum.SECKILL_NOT_START.getMessage();
        } else if (result == -2) {
            return ResultCodeEnum.SECKILL_HAS_END.getMessage();
        } else {
            return ResultCodeEnum.SECKILL_ROOM_NOT_EXIST.getMessage();
        }
    }


    /**
     * Redis Lua 脚本，用于实现秒杀库存预扣减的原子操作
     * 脚本执行逻辑：
     * 1. 时间校验：检查当前时间是否在秒杀活动开始和结束时间之间
     *    - 如果当前时间 < 开始时间，返回 -1（活动未开始）
     *    - 如果当前时间 > 结束时间，返回 -2（活动已结束）
     * 2. 库存校验：检查当前库存是否满足请求数量
     *    - 如果库存不足，返回 0（库存不足）
     * 3. 库存扣减：执行库存扣减操作
     *    - 扣减成功，返回 1（秒杀成功）
     * KEYS 参数说明：
     *    KEYS[1]: stockKey - 库存键，格式为 "seckill:stock:{roomId}"
     *    KEYS[2]: startKey - 开始时间键，格式为 "seckill:start:{roomId}"
     *    KEYS[3]: endKey - 结束时间键，格式为 "seckill:end:{roomId}"
     * ARGV 参数说明：
     *    ARGV[1]: currentTime - 当前系统时间（毫秒）
     *    ARGV[2]: requestCount - 本次请求的秒杀数量
     * 返回值说明：
     *    -1: 活动未开始
     *    -2: 活动已结束
     *    0: 库存不足
     *    1: 秒杀成功，库存已扣减
     */
    private static final String SECKILL_LUA_SCRIPT =
            "local stockKey = KEYS[1] " +
                    "local startKey = KEYS[2] " +
                    "local endKey = KEYS[3] " +
                    "local currentTime = tonumber(ARGV[1]) or 0 " + // 这里的 or 0 后面要有空格
                    "local requestCount = tonumber(ARGV[2]) or 0 " + // 这里的 or 0 后面要有空格
                    "local startTime = tonumber(redis.call('get', startKey) or 0) " +
                    "local endTime = tonumber(redis.call('get', endKey) or 0) " +
                    "if (currentTime < startTime) then return -1 end " +
                    "if (currentTime > endTime) then return -2 end " +
                    "local currentStock = tonumber(redis.call('get', stockKey) or 0) " +
                    "if (currentStock < requestCount) then return 0 end " +
                    "redis.call('decrby', stockKey, requestCount) " +
                    "return 1";

}