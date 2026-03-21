package com.sanjin.lease.web.admin.mapper;

import com.sanjin.lease.model.entity.GraphInfo;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author liubo
* @description 针对表【graph_info(图片信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.atguigu.lease.model.GraphInfo
*/
public interface GraphInfoMapper extends BaseMapper<GraphInfo> {

    List<GraphVo> findGraphListByApartmentId(@Param("itemType") ItemType itemType,@Param("id") Long id);

    List<GraphVo> findGraphListByRoomId(@Param("itemType") ItemType itemType, @Param("id") Long id);
}




