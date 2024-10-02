package com.xstocks.referral.task;


import com.xstocks.referral.pojo.RebatesRecord;
import com.xstocks.referral.pojo.ReferralUser;
import com.xstocks.referral.service.RebatesRecordService;
import com.xstocks.referral.service.ReferralUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.task-config", value = "enable", havingValue = "true")
public class RebatesUnfreezeTask {

    @Autowired
    private RebatesRecordService rebatesRecordService;

    @Autowired
    private ReferralUserService referralUserService;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void unfreezeData() {
        //查询创建时间是7天前，并且status=0的记录
        List<RebatesRecord> dataList = rebatesRecordService.getUnfreezeData();
        if (CollectionUtils.isEmpty(dataList))
            return;
        List<RebatesRecord> updateList = new ArrayList<>();
        for (RebatesRecord record : dataList) {
            record.setStatus(1);
            updateList.add(record);
            ReferralUser user = referralUserService.getUserId(record.getUserId());
            if (user == null) {
                log.error("用户不存在:{}", record.getUserId());
                continue;
            }
            user.setClaimableAmount(user.getClaimableAmount().add(record.getRebates()));
            user.setUpdateTime(LocalDateTime.now());
            referralUserService.updateById(user);
        }
        rebatesRecordService.updateBatchById(updateList);
    }
}
