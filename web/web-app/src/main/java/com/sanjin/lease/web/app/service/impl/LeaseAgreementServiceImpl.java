package com.sanjin.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sanjin.lease.common.context.LoginUserContext;
import com.sanjin.lease.model.entity.ApartmentInfo;
import com.sanjin.lease.model.entity.LeaseAgreement;
import com.sanjin.lease.model.entity.PaymentType;
import com.sanjin.lease.model.entity.RoomInfo;
import com.sanjin.lease.model.enums.ItemType;
import com.sanjin.lease.model.enums.LeaseStatus;
import com.sanjin.lease.model.entity.LeaseTerm;
import com.sanjin.lease.web.app.mapper.*;
import com.sanjin.lease.web.app.service.LeaseAgreementService;
import com.sanjin.lease.web.app.vo.agreement.AgreementDetailVo;
import com.sanjin.lease.web.app.vo.agreement.AgreementItemVo;
import com.sanjin.lease.web.app.vo.graph.GraphVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;


    @Override
    public List<AgreementItemVo> getAgreementVoListByPhone(String username) {

        return leaseAgreementMapper.listAgreementItemByPhone(username);
    }

    @Override
    public AgreementDetailVo getAgreementVoListById(Long id) {
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);

        if (leaseAgreement == null){
            return null;
        }
        ApartmentInfo apartmentInfo =
                apartmentInfoMapper.selectApartmentById(leaseAgreement.getApartmentId());

        List<GraphVo> arpartmentGraphoVoList =
                apartmentInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, leaseAgreement.getApartmentId());
        List<GraphVo> roomGraphVoList =
                graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, leaseAgreement.getRoomId());

        RoomInfo roomInfo = roomInfoMapper.selectById(id);

        PaymentType paymentType =
                paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());

        LeaseTerm leaseTerm =
                leaseTermMapper.selectLeaseTermById(leaseAgreement.getLeaseTermId());

        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(leaseAgreement, agreementDetailVo);
        agreementDetailVo.setApartmentName(apartmentInfo.getName());
        agreementDetailVo.setApartmentGraphVoList(arpartmentGraphoVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
        agreementDetailVo.setRoomNumber(roomInfo.getRoomNumber());
        agreementDetailVo.setPaymentTypeName(paymentType.getName());
        agreementDetailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
        agreementDetailVo.setLeaseTermUnit(leaseTerm.getUnit());
        return agreementDetailVo;
    }
}




