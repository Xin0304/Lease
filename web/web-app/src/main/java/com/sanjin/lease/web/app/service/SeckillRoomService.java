package com.sanjin.lease.web.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sanjin.lease.model.entity.SeckillRoom;
import com.sanjin.lease.web.app.dto.SeckillReqDTO;

public interface SeckillRoomService extends IService<SeckillRoom> {

    public String executeSeckill(SeckillReqDTO req);
}
