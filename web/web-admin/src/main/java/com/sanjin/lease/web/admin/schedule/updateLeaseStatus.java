package com.sanjin.lease.web.admin.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sanjin.lease.model.entity.LeaseAgreement;
import com.sanjin.lease.model.enums.LeaseStatus;
import com.sanjin.lease.web.admin.service.LeaseAgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class updateLeaseStatus {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 20 * * ? ")
    public void updateLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> wrapper = new LambdaUpdateWrapper<>();
        wrapper.lt(LeaseAgreement::getLeaseEndDate,new Date());
        wrapper.in(LeaseAgreement::getStatus,
                LeaseStatus.SIGNED,
                LeaseStatus.WITHDRAWING);
        wrapper.set(LeaseAgreement::getStatus,LeaseStatus.EXPIRED);
        leaseAgreementService.update(wrapper);
    }
}
