package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.UserKycMapper;
import com.xstocks.uc.pojo.KycImage;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.ApprovalKycParam;
import com.xstocks.uc.pojo.param.CreateKycParam;
import com.xstocks.uc.pojo.param.UserKycQueryParam;
import com.xstocks.uc.pojo.po.UserKycPO;
import com.xstocks.uc.pojo.vo.UserKycVo;
import com.xstocks.uc.service.KycImageService;
import com.xstocks.uc.service.UserKycService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Kevin
 * @date 2024/1/4 15:53
 * @apiNote user kyc impl
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class UserKycServiceImpl extends ServiceImpl<UserKycMapper, UserKycPO>
        implements UserKycService {

    @Autowired
    private KycImageService kycImageService;

    @Override
    public UserKycPO getByUerId(Long userId) {
        return getOne(Wrappers.<UserKycPO>lambdaQuery()
                .eq(UserKycPO::getUserId, userId).last("LIMIT 1"));
    }

    @Override
    public void userKyc(CreateKycParam kycParam) {
        UserKycPO userKycPO = getByUerId(kycParam.getUserId());

        boolean isExist = null != userKycPO;

        if (isExist && userKycPO.getState() == 0)
            throw new BizException(ErrorCode.ILLEGAL_STATE, "KYC in approval");


        if (isExist && userKycPO.getState() == 1)
            throw new BizException(ErrorCode.ILLEGAL_STATE, "KYC has approved");

        UserKycPO kycPO = new UserKycPO();
        BeanUtils.copyProperties(kycParam, kycPO);
        kycPO.setState(0);
        if (isExist) {
            kycPO.setId(userKycPO.getId());
            kycPO.setUpdateTime(new Date());
            this.updateById(kycPO);
            kycImageService.removeByKycId(kycPO.getId());
            saveKycImage(kycPO.getId(), kycParam.getCardImages());
        }
        this.save(kycPO);
        saveKycImage(kycPO.getId(), kycParam.getCardImages());
    }

    private void saveKycImage(Long kycId, List<String> list) {
        List<KycImage> imageList = new ArrayList<>();
        for (String url : list) {
            KycImage kycImage = new KycImage();
            kycImage.setKycId(kycId);
            kycImage.setImageUrl(url);
            imageList.add(kycImage);
        }
        kycImageService.saveBatch(imageList);
    }

    @Override
    public void approvalKyc(ApprovalKycParam approvalKycParam) {
        UserKycPO kycPO = this.getById(approvalKycParam.getId());
        if (null == kycPO)
            throw new BizException(ErrorCode.ILLEGAL_STATE, "KYC is not exist");

        if (kycPO.getState() != 0)
            throw new BizException(ErrorCode.ILLEGAL_STATE, String.format("Current state is %s ,can't approval", kycPO.getState() == 1 ? "审核通过" : "审核拒绝"));

        UserKycPO userKycPO = new UserKycPO();
        userKycPO.setId(kycPO.getId());
        userKycPO.setState(approvalKycParam.getState());
        userKycPO.setRemark(approvalKycParam.getRemark());
        userKycPO.setUpdateBy(approvalKycParam.getUpdateBy());
        this.updateById(userKycPO);
    }

    @Override
    public UserKycVo getKyc(Long userId) {
        UserKycPO userKycPO = getOne(Wrappers.lambdaQuery(UserKycPO.class).eq(UserKycPO::getUserId, userId).last("LIMIT 1"));
        if (null == userKycPO)
            throw new BizException(ErrorCode.ILLEGAL_STATE, "KYC is not exist");
        UserKycVo kycVo = new UserKycVo();
        BeanUtils.copyProperties(userKycPO, kycVo);
        kycVo.setCardImages(kycImageService.listByKycId(userKycPO.getId()));
        return kycVo;
    }

    @Override
    public IPage<UserKycPO> queryPageUserKyc(UserKycQueryParam kycQueryParam) {
        Page<UserKycPO> page = new Page<>(kycQueryParam.getPageNo(), kycQueryParam.getPageSize());
        page.addOrder(OrderItem.desc("update_time"), OrderItem.desc("create_time"));
        LambdaQueryWrapper<UserKycPO> wrapper = Wrappers.lambdaQuery();
        if (null != kycQueryParam.getState())
            wrapper.eq(UserKycPO::getState, kycQueryParam.getState());

        if (StringUtils.isNotEmpty(kycQueryParam.getUserName())) {
            wrapper.like(UserKycPO::getUserName, "%" + kycQueryParam.getUserName().trim() + "%");
        }
        return page(page, wrapper);
    }
}
