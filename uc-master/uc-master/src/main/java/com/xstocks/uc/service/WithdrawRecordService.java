package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.param.WithdrawRecordApplyParam;
import com.xstocks.uc.pojo.param.WithdrawRecordAuditParam;
import com.xstocks.uc.pojo.param.WithdrawRecordQueryParam;
import com.xstocks.uc.pojo.po.WithdrawRecordPO;

/**
 * <p>
 * 提现记录表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-01-08
 */
public interface WithdrawRecordService extends IService<WithdrawRecordPO> {

    IPage<WithdrawRecordPO> queryPageUserWithDraw(WithdrawRecordQueryParam recordQueryParam);

    void apply(WithdrawRecordApplyParam applyParam);

    void audit(WithdrawRecordAuditParam auditParam);
}
