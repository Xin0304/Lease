package com.sanjin.lease.web.app.mapper;

import com.sanjin.lease.model.entity.GraphInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.app.vo.graph.GraphVo;

import java.util.List;

/**
* @author liubo
* @description 针对表【graph_info(图片信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.atguigu.lease.model.entity.GraphInfo
*/
public interface GraphInfoMapper extends BaseMapper<GraphInfo> {


    List<GraphVo> findGraphinfoListByRoomId(ItemType itemType, Long id);

    List<GraphVo> selectListByItemTypeAndId(ItemType itemType, Long id);
}




