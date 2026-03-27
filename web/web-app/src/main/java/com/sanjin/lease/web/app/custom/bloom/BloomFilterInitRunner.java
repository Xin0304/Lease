package com.sanjin.lease.web.app.custom.bloom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sanjin.lease.model.entity.RoomInfo;
import com.sanjin.lease.web.app.mapper.RoomInfoMapper;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BloomFilterInitRunner implements CommandLineRunner {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Override
    public void run(String... args) throws Exception {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("bloom:room:id");
        // 2. 初始化：预计存入 1 万 条数据，允许的误判率为 0.01 (1%)
        // 注意：初始化只能做一次，先判断是否存在
        if (bloomFilter.isExists()) {
            System.out.println("房源 ID 布隆过滤器已存在，跳过初始化");
            return;
        }

        long count = roomInfoMapper.selectCount(null);
        // 不存在时才初始化
        bloomFilter.tryInit(count * 2, 0.01);

        List<Long> idList = roomInfoMapper.selectObjs(new QueryWrapper<RoomInfo>().select("id")
        ).stream().map(obj -> (Long) obj).collect(Collectors.toList());

        for (Long id : idList) {
            bloomFilter.add( id);
        }
    }
}
