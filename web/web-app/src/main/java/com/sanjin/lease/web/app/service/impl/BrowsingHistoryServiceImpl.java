package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sanjin.lease.model.entity.BrowsingHistory;
import com.sanjin.lease.web.app.mapper.BrowsingHistoryMapper;
import com.sanjin.lease.web.app.service.BrowsingHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.web.app.vo.history.HistoryItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {

    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;


    @Override
    public IPage<HistoryItemVo> pageHistoryItemByUserId(IPage<HistoryItemVo> page, Long userId) {

        return browsingHistoryMapper.pageHistoryItemByUserId(page, userId);
    }

    @Async
    @Override
    public void saveHistory(Long userId, Long id) {


    }

}




