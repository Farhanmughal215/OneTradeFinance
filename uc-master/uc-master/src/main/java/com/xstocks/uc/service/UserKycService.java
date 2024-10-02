package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.param.ApprovalKycParam;
import com.xstocks.uc.pojo.param.CreateKycParam;
import com.xstocks.uc.pojo.param.UserKycQueryParam;
import com.xstocks.uc.pojo.po.UserKycPO;
import com.xstocks.uc.pojo.vo.UserKycVo;

/**
 * @author Kevin
 * @date 2024/1/4 15:23
 * @apiNote kyc
 */
public interface UserKycService extends IService<UserKycPO> {

    UserKycPO getByUerId(Long userId);

    void userKyc(CreateKycParam kycParam);

    void approvalKyc(ApprovalKycParam approvalKycParam);

    UserKycVo getKyc(Long userId);

    IPage<UserKycPO> queryPageUserKyc(UserKycQueryParam kycQueryParam);
}
