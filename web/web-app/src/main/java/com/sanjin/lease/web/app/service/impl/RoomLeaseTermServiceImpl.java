package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.model.entity.LeaseTerm;
import com.sanjin.lease.model.entity.RoomLeaseTerm;
import com.sanjin.lease.web.app.service.RoomLeaseTermService;
import com.sanjin.lease.web.app.mapper.RoomLeaseTermMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【room_lease_term(房间租期管理表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class RoomLeaseTermServiceImpl extends ServiceImpl<RoomLeaseTermMapper, RoomLeaseTerm>
    implements RoomLeaseTermService{

    @Autowired
    private RoomLeaseTermMapper roomLeaseTermMapper;
    @Override
    public List<LeaseTerm> getRoomLeaseTermById(Long id) {
        return roomLeaseTermMapper.getRoomLeaseTermById(id);
    }
}




