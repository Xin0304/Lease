package com.sanjin.lease.web.app.service;

import com.sanjin.lease.model.entity.LeaseTerm;
import com.sanjin.lease.model.entity.RoomLeaseTerm;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author liubo
* @description 针对表【room_lease_term(房间租期管理表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface RoomLeaseTermService extends IService<RoomLeaseTerm> {

    List<LeaseTerm> getRoomLeaseTermById(Long id);
}
