package com.sanjin.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.app.BloomEnum;
import com.sanjin.lease.common.app.RoomEnum;
import com.sanjin.lease.common.utils.CacheClient;
import com.sanjin.lease.model.entity.*;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.web.admin.mapper.*;
import com.sanjin.lease.web.admin.service.*;
import com.sanjin.lease.web.admin.vo.attr.AttrValueVo;
import com.sanjin.lease.web.admin.vo.graph.GraphVo;
import com.sanjin.lease.web.admin.vo.room.RoomDetailVo;
import com.sanjin.lease.web.admin.vo.room.RoomItemVo;
import com.sanjin.lease.web.admin.vo.room.RoomQueryVo;
import com.sanjin.lease.web.admin.vo.room.RoomSubmitVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Slf4j
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    //配套
    @Autowired
    private RoomFacilityService roomFacilityService;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    //租期
    @Autowired
    private RoomLeaseTermService roomLeaseTermService;
    @Autowired
    private LeaseTermMapper leaseTermMapper;

    //标签
    @Autowired
    private RoomLabelService roomLabelService;
    @Autowired
    private LabelInfoMapper labelInfoMapper;

    //支付方式
    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;
    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    //属性
    @Autowired
    private RoomAttrValueService roomAttrValueService;
    @Autowired
    private AttrValueMapper attrValueMapper;

    //图片
    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CacheClient cacheClient;

    @Override
    public RoomInfo saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isUpdate = roomSubmitVo.getId() != null;
        this.saveOrUpdate(roomSubmitVo);

        cacheClient.delete(RoomEnum.ROOM_INFO_KEY + roomSubmitVo.getId());

        if (isUpdate){
            removeApartmentAllById(roomSubmitVo);
        }
        RoomSubmitVo SaveRoomSubmitVo = getRoomSubmitVoById(roomSubmitVo);
        RBloomFilter<Long> bloomFilter =
                redissonClient.getBloomFilter(BloomEnum.BLOOM_ROOM_KEY);
        bloomFilter.add(roomSubmitVo.getId());
        return SaveRoomSubmitVo;
    }


    //抽出删除方法
    private void removeApartmentAllById(RoomSubmitVo roomSubmitVo) {
        //配套删除
        LambdaQueryWrapper<RoomFacility> facilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        facilityLambdaQueryWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
        roomFacilityService.remove(facilityLambdaQueryWrapper);

        //属性
        LambdaQueryWrapper<RoomAttrValue> valueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        valueLambdaQueryWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
        roomAttrValueService.remove(valueLambdaQueryWrapper);

        //租期
        LambdaQueryWrapper<RoomLeaseTerm> leaseTermLambdaQueryWrapper = new LambdaQueryWrapper<>();
        leaseTermLambdaQueryWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
        roomLeaseTermService.remove(leaseTermLambdaQueryWrapper);

        //标签
        LambdaQueryWrapper<RoomLabel> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        labelLambdaQueryWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
        roomLabelService.remove(labelLambdaQueryWrapper);

        //支付方式
        LambdaQueryWrapper<RoomPaymentType> paymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        paymentTypeLambdaQueryWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
        roomPaymentTypeService.remove(paymentTypeLambdaQueryWrapper);

        //图片
        LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
        graphInfoService.remove(graphInfoLambdaQueryWrapper);


    }

    private RoomSubmitVo getRoomSubmitVoById(RoomSubmitVo roomSubmitVo) {
        //配套
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)) {
            ArrayList<RoomFacility> facilityArrayList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(roomSubmitVo.getId());
                roomFacility.setFacilityId(facilityInfoId);
                facilityArrayList.add(roomFacility);
            }
            roomFacilityService.saveBatch(facilityArrayList);
        }

        //租期
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if (!CollectionUtils.isEmpty(leaseTermIds)) {
            List<RoomLeaseTerm> roomLeaseTermArrayList = new ArrayList<>();
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = new RoomLeaseTerm();
                roomLeaseTerm.setRoomId(roomSubmitVo.getId());
                roomLeaseTerm.setLeaseTermId(leaseTermId);
                roomLeaseTermArrayList.add(roomLeaseTerm);
            }

            roomLeaseTermService.saveBatch(roomLeaseTermArrayList);
        }

        //标签
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelInfoIds)) {
            List<RoomLabel> roomLabelArrayList = new ArrayList<>();
            for (Long labelInfoId : labelInfoIds) {
                RoomLabel roomLabel = new RoomLabel();
                roomLabel.setRoomId(roomSubmitVo.getId());
                roomLabel.setLabelId(labelInfoId);
                roomLabelArrayList.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabelArrayList);
        }

        //支付方式
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if (!CollectionUtils.isEmpty(paymentTypeIds)) {
            List<RoomPaymentType> roomPaymentTypeList = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = new RoomPaymentType();
                roomPaymentType.setRoomId(roomSubmitVo.getId());
                roomPaymentType.setPaymentTypeId(paymentTypeId);
                roomPaymentTypeList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypeList);

        }

        //属性
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if (!CollectionUtils.isEmpty(attrValueIds)) {
            List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = new RoomAttrValue();
                roomAttrValue.setRoomId(roomSubmitVo.getId());
                roomAttrValue.setAttrValueId(attrValueId);
                roomAttrValueList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueList);
        }

        // 图片
        List<GraphVo> graphVos = roomSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVos)) {
            List<GraphInfo> graphList = new ArrayList<>();
            for (GraphVo graphVo : graphVos) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setUrl(graphVo.getUrl());
                graphInfo.setItemId(roomSubmitVo.getId());
                graphList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphList);
        }
        return roomSubmitVo;

    }
    @Override
    public RoomDetailVo getRoomDetailById(Long id) {

        RoomInfo roomInfo = this.getById(id);
        if (roomInfo == null){
            return null;
        }
        //查询公寓
        ApartmentInfo apartmentInfo =
                apartmentInfoMapper.selectById(roomInfo.getApartmentId());

        //查询配套
        List<FacilityInfo> facilityInfoList =
                facilityInfoMapper.findFacilityListByRoomId(id);


        //查询标签
        List<LabelInfo> labelInfoList =
                labelInfoMapper.findLabelListByRoomId(id);

        //查询租期
        List<LeaseTerm> leaseTermList =
                leaseTermMapper.findLeaseTermListByRoomId(id);

        //查询支付方式
        List<PaymentType> paymentTypeList =
                paymentTypeMapper.findPaymentTypeListByRoomId(id);

        //查询属性
        List<AttrValueVo>  attrValueDetailVo =
                attrValueMapper.findAttrValueListByRoomId(id);

        // 图片
        List<GraphVo>  graphDetailVo =
                graphInfoMapper.findGraphListByRoomId(ItemType.ROOM,id);

        RoomDetailVo adminRoomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo,adminRoomDetailVo);
        adminRoomDetailVo.setApartmentInfo(apartmentInfo);
        adminRoomDetailVo.setGraphVoList(graphDetailVo);
        adminRoomDetailVo.setAttrValueVoList(attrValueDetailVo);
        adminRoomDetailVo.setFacilityInfoList(facilityInfoList);
        adminRoomDetailVo.setLabelInfoList(labelInfoList);
        adminRoomDetailVo.setPaymentTypeList(paymentTypeList);
        adminRoomDetailVo.setLeaseTermList(leaseTermList);


        return adminRoomDetailVo;
    }

    //分页查询
    @Override
    public IPage<RoomItemVo> selectApartmentInfoPage(IPage<RoomItemVo> page,
                                                     RoomQueryVo queryVo) {

        return roomInfoMapper.selectApartmentInfoPage(page, queryVo);
    }


    @Override
    public void removeApartmentById(Long id) {
        //删除房间
        this.removeById(id);

        LambdaQueryWrapper<RoomInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomInfo::getId, id);

        RoomInfo updateEntity = new RoomInfo();
        updateEntity.setIsDeleted((byte) 1); // 假设 1 代表已删除
        this.baseMapper.update(updateEntity, wrapper);

        //删除配套
        LambdaQueryWrapper<RoomFacility> facilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        facilityLambdaQueryWrapper.eq(RoomFacility::getRoomId,id);
        roomFacilityService.remove(facilityLambdaQueryWrapper);


        //删除租期
        LambdaQueryWrapper<RoomLeaseTerm> leaseTermLambdaQueryWrapper = new LambdaQueryWrapper<>();
        leaseTermLambdaQueryWrapper.eq(RoomLeaseTerm::getRoomId,id);
        roomLeaseTermService.remove(leaseTermLambdaQueryWrapper);

        //删除标签
        LambdaQueryWrapper<RoomLabel> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        labelLambdaQueryWrapper.eq(RoomLabel::getRoomId,id);
        roomLabelService.remove(labelLambdaQueryWrapper);

        //删除支付方式
        LambdaQueryWrapper<RoomPaymentType> paymentTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        paymentTypeLambdaQueryWrapper.eq(RoomPaymentType::getRoomId,id);
        roomPaymentTypeService.remove(paymentTypeLambdaQueryWrapper);

        //删除属性
        LambdaQueryWrapper<RoomAttrValue> valueLambdaQueryWrapper = new LambdaQueryWrapper<>();
        valueLambdaQueryWrapper.eq(RoomAttrValue::getRoomId,id);
        roomAttrValueService.remove(valueLambdaQueryWrapper);

        //删除图片
        LambdaQueryWrapper<GraphInfo> graphInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemType,ItemType.ROOM);
        graphInfoLambdaQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphInfoLambdaQueryWrapper);

        cacheClient.delete(RoomEnum.ROOM_INFO_KEY + id);

        log.info("房间逻辑删除成功，ID: {}, 布隆过滤器未清理", id);
    }
}




