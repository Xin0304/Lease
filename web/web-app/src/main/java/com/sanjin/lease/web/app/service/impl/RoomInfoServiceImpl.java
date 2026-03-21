package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sanjin.lease.model.entity.*;
import com.sanjin.lease.model.entity.RoomInfo;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.model.enums.LeaseStatus;
import com.sanjin.lease.web.app.mapper.*;
import com.sanjin.lease.web.app.mapper.RoomInfoMapper;
import com.sanjin.lease.web.app.service.ApartmentInfoService;
import com.sanjin.lease.web.app.service.RoomInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.web.app.vo.apartment.ApartmentItemVo;
import com.sanjin.lease.web.app.vo.attr.AttrValueVo;
import com.sanjin.lease.web.app.vo.fee.FeeValueVo;
import com.sanjin.lease.web.app.vo.graph.GraphVo;
import com.sanjin.lease.web.app.vo.room.RoomDetailVo;
import com.sanjin.lease.web.app.vo.room.RoomItemVo;
import com.sanjin.lease.web.app.vo.room.RoomQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {


    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private ApartmentInfoService apartmentInfoService;
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;
    @Override
    public RoomDetailVo getRoomDetailById(Long id) {
        RoomInfo roomInfo = roomInfoMapper.selectRoomById(id);
        if (roomInfo == null){
            return null;
        }
        //2.查询所属公寓信息
        ApartmentItemVo apartmentItemVo =
                apartmentInfoService.selectApartmentItemVoById(roomInfo.getApartmentId());
        //3.查询graphInfoList
        List<GraphVo> graphInfoList =
                graphInfoMapper.findGraphinfoListByRoomId(ItemType.ROOM,id);

        //4.查询attrValueList
        List<AttrValueVo> attrValueList =
                attrValueMapper.findAttrValueListByRoomId(id);
        //5.查询facilityInfoList
        List<FacilityInfo> facilityInfoList =
                facilityInfoMapper.findFacilityInfoListByRoomId(id);
        //6.查询labelInfoList
        List<LabelInfo> labelInfoList =
                labelInfoMapper.findLabelInfoListByRoomId(id);

        //7.查询paymentTypeList
        List<PaymentType> paymentTypeList =
                paymentTypeMapper.findPaymentTypeListById(id);
        //8.查询leaseTermList
        List<LeaseTerm> leaseTermList =
                leaseTermMapper.findLeaseTermListByRoomId(id);
        //9.查询费用项目信息
        List<FeeValueVo> feeValueVos =
                feeValueMapper.findFeeValueListByRoomId(id);

        //10.查询房间入住状态
        LambdaQueryWrapper<LeaseAgreement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseAgreement::getRoomId, roomInfo.getId());
        queryWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING);
        Long singedCount = leaseAgreementMapper.selectCount(queryWrapper);

        RoomDetailVo appRoomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, appRoomDetailVo);
        appRoomDetailVo.setIsDelete(roomInfo.getIsDeleted() == 1);
        appRoomDetailVo.setIsCheckIn(singedCount > 0);

        appRoomDetailVo.setApartmentItemVo(apartmentItemVo);
        appRoomDetailVo.setGraphVoList(graphInfoList);
        appRoomDetailVo.setAttrValueVoList(attrValueList);
        appRoomDetailVo.setFacilityInfoList(facilityInfoList);
        appRoomDetailVo.setLabelInfoList(labelInfoList);
        appRoomDetailVo.setPaymentTypeList(paymentTypeList);
        appRoomDetailVo.setFeeValueVoList(feeValueVos);
        appRoomDetailVo.setLeaseTermList(leaseTermList);

        return appRoomDetailVo;
    }


    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(Page<RoomItemVo> page, Long id) {
        return roomInfoMapper.pageItemByApartmentId(page,id);
    }

    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomItemByQuery(page, queryVo);
    }
}




