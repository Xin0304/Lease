package com.sanjin.lease.web.admin.service;

import com.sanjin.lease.model.entity.ApartmentInfo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【apartment_info(公寓信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface ApartmentInfoService extends IService<ApartmentInfo> {

    ApartmentInfo saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo);

    ApartmentDetailVo getApartmentDetailById(Long id);

    Page<ApartmentItemVo> selectApartmentInfoPage(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo);

    void removeApartmentById(Long id);
}
