package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.model.entity.ApartmentInfo;
import com.sanjin.lease.model.entity.FacilityInfo;
import com.sanjin.lease.model.entity.LabelInfo;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.app.mapper.*;
import com.sanjin.lease.web.app.service.ApartmentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.sanjin.lease.web.app.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.app.vo.apartment.ApartmentQueryVo;
import com.sanjin.lease.web.app.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Override
    public ApartmentItemVo selectApartmentItemVoById(Long id) {
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectApartmentById(id);

        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();

        BeanUtils.copyProperties(apartmentInfo, apartmentItemVo);
        apartmentItemVo.setGraphVoList(graphVoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(minRent);

        return apartmentItemVo;
    }


    @Override
    public ApartmentItemVo getApartmentItemVoById(Long apartmentId) {
        return apartmentInfoMapper.selectApartmentById(apartmentId);
    }
    //分页查询公寓信息
    @Override
    public IPage<ApartmentItemVo> pageApartmentItemVo(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.selectPageApartment(page, queryVo);
    }

    @Override
    public ApartmentDetailVo getApartmentDetailById(Long id) {
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectApartmentById(id);
        if (apartmentInfo == null){
            return null;
        }

        //查询图片列表
        List<GraphVo> graphVoList =
                graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        //查询标签列表
        List<LabelInfo> labelInfoList =
                labelInfoMapper.selectListByApartmentId(id);

        //查询配套列表
        List<FacilityInfo> facilityInfoList =
                facilityInfoMapper.selectListByApartmentId(id);

        //查询租金最小值
        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);


        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();

        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setMinRent(minRent);

        return apartmentDetailVo;

    }
}




