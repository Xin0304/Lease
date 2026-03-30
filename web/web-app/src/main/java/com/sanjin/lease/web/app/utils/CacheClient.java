package com.sanjin.lease.web.app.utils;


import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheClient {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 缓存数据
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value),time, unit);
    }

    // 查缓存
    public <T> T get(String  key, Class<T> clazz){
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isEmpty()){
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    // 删除缓存
    public void delete(String key){
        stringRedisTemplate.delete(key);
    }

}
