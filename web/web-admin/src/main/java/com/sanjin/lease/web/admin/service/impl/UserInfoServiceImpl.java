package com.sanjin.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.model.entity.UserInfo;
import com.sanjin.lease.model.enums.BaseStatus;
import com.sanjin.lease.web.admin.controller.user.UserInfoController;
import com.sanjin.lease.web.admin.service.UserInfoService;
import com.sanjin.lease.web.admin.mapper.UserInfoMapper;
import com.sanjin.lease.web.admin.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author liubo
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2023-07-24 15:48:00
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Override
    public IPage<UserInfo> selectUserInfoPage(Page<UserInfo> page, UserInfoQueryVo queryVo) {
        LambdaUpdateWrapper<UserInfo> userInfoWrapper = new LambdaUpdateWrapper<>();
        userInfoWrapper.like(queryVo.getStatus()!=null,
                UserInfo::getStatus, queryVo.getStatus());
        userInfoWrapper.like(queryVo.getPhone()!=null,
                UserInfo::getPhone,queryVo.getPhone());
        Page<UserInfo> pageModel = this.page(page, userInfoWrapper);
        return pageModel;
    }
}




