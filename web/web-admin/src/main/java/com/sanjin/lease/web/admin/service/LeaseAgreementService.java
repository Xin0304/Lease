package com.sanjin.lease.web.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sanjin.lease.model.entity.LeaseAgreement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sanjin.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.sanjin.lease.web.admin.vo.agreement.AgreementVo;

import java.util.List;

/**
* @author liubo
* @description 针对表【lease_agreement(租约信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface LeaseAgreementService extends IService<LeaseAgreement> {

    IPage<AgreementVo> pageAgreementByQuery(IPage<AgreementVo> page, AgreementQueryVo queryVo);


    AgreementVo getAgreementById(Long id);
}
