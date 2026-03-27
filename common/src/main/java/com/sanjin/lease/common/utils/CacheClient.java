package com.sanjin.lease.common.utils;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存客户端工具类
 * 提供统一的缓存操作方法，包括设置、获取、删除等功能
 * 所有操作都会在控制台打印日志，便于观察缓存的使用情况
 */
@Component
@Slf4j
public class CacheClient {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将数据缓存到 Redis 中
     *
     * @param key   缓存的键，用于唯一标识缓存数据
     * @param value 要缓存的数据对象，可以是任意类型
     * @param time  缓存的过期时间数值
     * @param unit  缓存的过期时间单位（如秒、分钟、小时等）
     *
     * 操作流程：
     * 1. 将 Java 对象通过 FastJSON2 序列化为 JSON 字符串
     * 2. 将 JSON 字符串存储到 Redis 中
     * 3. 设置指定的过期时间
     * 4. 在控制台打印日志，记录缓存的 key 和过期时间
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        // 将对象转换为 JSON 字符串
        String json = JSON.toJSONString(value);
        // 执行 Redis set 操作
        stringRedisTemplate.opsForValue().set(key, json, time, unit);

        // 打印日志，记录缓存操作
        log.info("[缓存写入] key: {}, 过期时间：{} {}",
                key,  time, unit);
    }

    // 建议修改 CacheClient，增加随机 TTL 方法
    public void setWithRandomTTL(String key, Object value,
                                 long baseTime,
                                 long randomRange,
                                 TimeUnit unit) {
        long randomTTL = baseTime + ThreadLocalRandom.current()
                .nextLong(-randomRange, randomRange);
        set(key, value, randomTTL, unit);

    }
    /**
     * 从 Redis 缓存中获取数据
     *
     * @param key   缓存的键，用于查找缓存数据
     * @param clazz 期望返回的数据类型 Class，用于反序列化
     * @param <T>   泛型类型，表示期望返回的类型
     * @return 如果缓存存在，返回对应的数据对象；如果缓存不存在或已过期，返回 null
     *
     * 操作流程：
     * 1. 根据 key 从 Redis 中获取 JSON 字符串
     * 2. 判断获取的字符串是否为空
     * 3. 如果不为空，将 JSON 字符串反序列化为指定类型的对象
     * 4. 在控制台打印日志，记录是否命中缓存
     */
    public <T> T get(String key, Class<T> clazz) {
        // 从 Redis 获取缓存的 JSON 字符串
        String json = stringRedisTemplate.opsForValue().get(key);


        // 判断缓存是否存在
        if (json == null ) {
            log.info("[缓存未命中] key: {}", key);
            return null;
        }
        if (json.equals("null")){
            log.info("[缓存命中空值] key: {}", key);
            return null;
        }
        // 打印日志，记录缓存命中
        log.info("[缓存命中] key: {}", key);
        // 将 JSON 字符串转换为指定类型对象并返回
        return JSON.parseObject(json, clazz);
    }

    /**
     * 从 Redis 中删除指定的缓存数据
     * 
     * @param key 要删除的缓存键
     * 
     * 操作流程：
     * 1. 根据 key 从 Redis 中删除对应的缓存数据
     * 2. 在控制台打印日志，记录删除操作的结果
     */
    public void delete(String key) {
        // 执行 Redis delete 操作
        Boolean deleted = stringRedisTemplate.delete(key);
        
        // 打印日志，记录删除操作结果
        if (deleted != null && deleted) {
            log.info("[缓存删除成功] key: {}", key);
        } else {
            log.warn("[缓存删除失败] key: {}（可能不存在）", key);
        }
    }
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
