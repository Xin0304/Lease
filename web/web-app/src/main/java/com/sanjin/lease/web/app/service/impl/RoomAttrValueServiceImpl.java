package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.model.entity.RoomAttrValue;
import com.sanjin.lease.web.app.service.RoomAttrValueService;
import com.sanjin.lease.web.app.mapper.RoomAttrValueMapper;
import org.springframework.stereotype.Service;

/**
 * 针对表【room_attr_value(房间&基本属性值关联表)】的数据库操作Service实现
* @author liubo
* * @literal  2023-07-26 11:12:39
*/
@Service
public class RoomAttrValueServiceImpl extends ServiceImpl<RoomAttrValueMapper, RoomAttrValue>
    implements RoomAttrValueService{

}




