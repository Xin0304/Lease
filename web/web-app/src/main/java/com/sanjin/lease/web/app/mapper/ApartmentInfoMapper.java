package com.sanjin.lease.web.app.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.model.entity.ApartmentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.sanjin.lease.web.app.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.app.vo.apartment.ApartmentQueryVo;
import com.sanjin.lease.web.app.vo.graph.GraphVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author liubo
* @description 针对表【apartment_info(公寓信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.atguigu.lease.model.entity.ApartmentInfo
*/
public interface ApartmentInfoMapper extends BaseMapper<ApartmentInfo> {

    IPage<ApartmentItemVo> selectPageApartment(@Param("page") Page<ApartmentItemVo> page,
                                               @Param("queryVo") ApartmentQueryVo queryVo);

    ApartmentItemVo selectApartmentById(Long apartmentId);

    List<GraphVo> selectListByItemTypeAndId(ItemType itemType, Long apartmentId);
}




