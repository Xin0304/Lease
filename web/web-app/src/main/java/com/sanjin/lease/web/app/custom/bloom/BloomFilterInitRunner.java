package com.sanjin.lease.web.app.custom.bloom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.common.app.BloomEnum;
import com.sanjin.lease.model.entity.RoomInfo;
import com.sanjin.lease.web.app.mapper.RoomInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BloomFilterInitRunner implements ApplicationRunner {

    private final RedissonClient redissonClient;
    
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    /**
     * 应用启动时初始化布隆过滤器，将所有房间 ID 加载到布隆过滤器中
     * 用于快速判断某个房间 ID 是否存在，提高查询效率
     * 
     * <p>执行流程：</p>
     * <ol>
     *     <li>检查主布隆过滤器是否已存在，避免重复初始化</li>
     *     <li>查询数据库中未删除的房间总数，计算预期容量</li>
     *     <li>初始化主布隆过滤器，设置预期元素数量和误判率（0.01）</li>
     *     <li>分页查询所有未删除的房间 ID，逐个添加到主布隆过滤器中</li>
     *     <li>检查已删除布隆过滤器是否已存在，避免重复初始化</li>
     *     <li>查询数据库中已删除的房间总数，初始化已删除布隆过滤器</li>
     *     <li>分页查询所有已删除的房间 ID，逐个添加到已删除布隆过滤器中</li>
     * </ol>
     *
     * @param args 应用程序启动参数，可用于接收命令行参数
     * @throws Exception 初始化过程中可能抛出的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化房源 ID 布隆过滤器...");
            
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(BloomEnum.BLOOM_ROOM_KEY);
    
        // 初始化主布隆过滤器（存储未删除的房间 ID）
        if (!bloomFilter.isExists()) {
            log.info("主布隆过滤器不存在，开始初始化...");
               
            long total = roomInfoMapper.selectCount(new QueryWrapper<RoomInfo>().eq("is_deleted", 0));
            long expectedInsertions = Math.max(total * 2, 1000L);
            bloomFilter.tryInit(expectedInsertions, 0.01);
            log.info("布隆过滤器初始化完成，预期容量：{}, 误判率：0.01", expectedInsertions);
            
            // 分页加载所有未删除的房间 ID 到主过滤器
            loadToBloomFilter(bloomFilter, new QueryWrapper<RoomInfo>().eq("is_deleted", 0).select("id"));
        } else {
            log.info("房源 ID 布隆过滤器已存在，跳过初始化");
        }
    }


    /**
     * 分页查询房间 ID 并加载到布隆过滤器中
     * 
     * <p>该方法采用分页查询的方式，避免一次性加载大量数据到内存，
     * 然后将每页的房间 ID 逐个添加到布隆过滤器中。</p>
     *
     * @param bloomFilter 目标布隆过滤器实例，用于存储房间 ID
     * @param queryWrapper MyBatis-Plus 查询条件封装，用于指定查询的房间 ID 集合
     */
    public void loadToBloomFilter(RBloomFilter<Long> bloomFilter, QueryWrapper<RoomInfo> queryWrapper){
        Long pageSize = 1000L;
        long total = roomInfoMapper.selectCount(queryWrapper);
        // 计算总页数并分页加载
        for (int i = 0; i < (total + pageSize - 1) / pageSize; i++) {
            Page<RoomInfo> page = new Page<>(i + 1, pageSize);
            Page<RoomInfo> roomInfoPage = roomInfoMapper.selectPage(page, queryWrapper);
            roomInfoPage.getRecords().forEach(roomInfo -> bloomFilter.add(roomInfo.getId()));
        }
        log.info("已加载 {} 个房间 ID", total);
    }
}
