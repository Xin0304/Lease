package com.sanjin.lease.web.admin.service.impl;


import com.sanjin.lease.model.entity.*;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.admin.mapper.*;
import com.sanjin.lease.web.admin.service.*;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.sanjin.lease.web.admin.vo.apartment.ApartmentSubmitVo;

import com.sanjin.lease.web.admin.vo.fee.FeeValueVo;
import com.sanjin.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {
    //公寓&配套关系
    @Autowired
    private ApartmentFacilityService apartmentFacilityService;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    //公寓&杂费关联
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private FeeValueMapper feeValueMapper;
    //公寓杂费


    //公寓标签关联表
    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private LabelInfoMapper labelInfoMapper;

    //图片信息
    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;


    @Override
    public ApartmentInfo saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        boolean isUpdate = apartmentSubmitVo.getId() !=null;
        //保存更新公寓信息
        //1 添加 ApartmentInfo公寓基本数据到 apartment_info：公寓基本信息表
        this.saveOrUpdate(apartmentSubmitVo);

            // 删除该公寓的所有关联数据（配套、标签、杂费、图片），以便重新插入最新数据
        if (isUpdate){
            // 删除公寓配套信息
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentSubmitVo.getId());
            apartmentFacilityService.remove(facilityQueryWrapper);

            // 删除公寓标签信息
            LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            apartmentLabelService.remove(labelQueryWrapper);

            // 删除公寓杂费信息
            LambdaQueryWrapper<ApartmentFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
            feeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(feeValueQueryWrapper);

            // 删除公寓图片信息
            LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoQueryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId())
                    .eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoService.remove(graphInfoQueryWrapper);
        }


        //2 添加公寓配套数据 apartment_facility: 公寓配套信息数据
        // 一个公寓id 对应多个配套数据，添加多条记录
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            ArrayList<ApartmentFacility> facilityArrayList = new ArrayList<>();
            for (Long facilityId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityId);
                facilityArrayList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(facilityArrayList);
        }
        //3 添加公寓的标签数据  apartment_label：公寓标签数据
        //一个公寓id 对应多个标签数据，添加多条记录
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)){
            ArrayList<ApartmentLabel> labelArrayList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                labelArrayList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(labelArrayList);

        }

        //4 添加公寓杂费数据  apartment_fee_value：公寓杂费数据
        //一个公寓id 对应多个杂费数据，添加多条记录
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)){
            ArrayList<ApartmentFeeValue> feeValueArrayList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                feeValueArrayList.add(apartmentFeeValue);
            }

            apartmentFeeValueService.saveBatch(feeValueArrayList);
        }


        //5 添加公寓图片数据 graph_info：图片表graph_info
        //添加多条记录 添加图片类型 （公寓图片还是房间图片），公寓id
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphArrayList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphArrayList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphArrayList);
        }
        return apartmentSubmitVo;
    }


    //根据ID 查询公寓详情
    @Override
    public ApartmentDetailVo getApartmentDetailById(Long id) {
        //1 根据公寓id查询公寓基本信息
        ApartmentInfo apartmentInfo = this.getById(id);
        if (apartmentInfo == null){
           return null;
        }

        //2 根据公寓id查询公寓配套数据
        List<FacilityInfo> facilityInfoList =
                facilityInfoMapper.findFacilityListByApartmentId(id);
        //3 根据公寓id查询标签数据
        List<LabelInfo> labelListByApartmentId =
                labelInfoMapper.findLabelListByApartmentId(id);
        //4 根据公寓id查询杂费数据
        List<FeeValueVo> feeValueListByApartmentId =
                feeValueMapper.findFeeValueListByApartmentId(id);
        //5 根据公寓id查询图片数据
        List<GraphVo> graphListByApartmentId =
                graphInfoMapper.findGraphListByApartmentId(ItemType.APARTMENT,id);
        // 6 把上面查询出来所有数据封装到ApartmentDetailVo对象
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphListByApartmentId);
        apartmentDetailVo.setLabelInfoList(labelListByApartmentId);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueListByApartmentId);

        //7 返回ApartmentDetailVo对象
        return apartmentDetailVo;
    }

    @Override
    public Page<ApartmentItemVo> selectApartmentInfoPage(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageApartmentItemByQuery(page, queryVo);
    }

    @Autowired
    private RoomInfoService roomInfoService;
    @Override
    public void removeApartmentById(Long id) {

        LambdaQueryWrapper<RoomInfo> roomInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoLambdaQueryWrapper.eq(RoomInfo::getApartmentId,id);
        long count = roomInfoService.count(roomInfoLambdaQueryWrapper);
        if (count > 0){
            throw new RuntimeException("存在房间信息，不能删除");
        }

        this.removeById(id);

        LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,id);
        apartmentFacilityService.remove(facilityQueryWrapper);

        LambdaQueryWrapper<ApartmentLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(lambdaQueryWrapper);

        LambdaQueryWrapper<ApartmentFeeValue> feeValueQueryWrapper = new LambdaQueryWrapper<>();
        feeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(feeValueQueryWrapper);

        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphInfoService.remove(graphQueryWrapper);

    }
}




