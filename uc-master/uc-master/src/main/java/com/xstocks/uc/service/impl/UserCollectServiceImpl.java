package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.UserCollectMapper;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.CollectBaseParam;
import com.xstocks.uc.pojo.po.UserCollectPO;
import com.xstocks.uc.service.UserCollectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author dongjunfu
 * @description 针对表【user_collect(user collect)】的数据库操作Service实现
 * @createDate 2023-11-14 15:55:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserCollectServiceImpl extends ServiceImpl<UserCollectMapper, UserCollectPO>
        implements UserCollectService {

    @Override
    public boolean userCollect(CollectBaseParam collectParam) {
        if (Objects.isNull(collectParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userId required");
        }
        if (Objects.isNull(collectParam.getTickerId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "tickerId required");
        }
        if (Objects.isNull(collectParam.getIsDel())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "isDel required");
        }
        if (collectParam.getIsDel() != 0 && collectParam.getIsDel() != 1) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "isDel invalid. 0true 1false");
        }
        UserCollectPO userCollectPO = getOne(Wrappers.<UserCollectPO>lambdaQuery()
                .eq(UserCollectPO::getUserId, collectParam.getUserId())
                .eq(UserCollectPO::getTickerId, collectParam.getTickerId()).last("LIMIT 1"));
        if (Objects.isNull(userCollectPO)) {
            userCollectPO = new UserCollectPO();
            userCollectPO.setUserId(collectParam.getUserId());
            userCollectPO.setTickerId(collectParam.getTickerId());
            userCollectPO.setCreateTime(new Date());
            userCollectPO.setUpdateTime(new Date());
            userCollectPO.setIsDel(collectParam.getIsDel());
            return save(userCollectPO);
        } else {
            userCollectPO.setUpdateTime(new Date());
            userCollectPO.setIsDel(collectParam.getIsDel());
            return updateById(userCollectPO);
        }
    }

    @Override
    public boolean isUserCollect(CollectBaseParam collectParam) {
        if (Objects.isNull(collectParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userId required");
        }
        if (Objects.isNull(collectParam.getTickerId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "tickerId required");
        }
        UserCollectPO userCollectPO = getOne(Wrappers.<UserCollectPO>lambdaQuery()
                .eq(UserCollectPO::getUserId, collectParam.getUserId())
                .eq(UserCollectPO::getTickerId, collectParam.getTickerId()).last("LIMIT 1"));
        if (Objects.nonNull(userCollectPO)) {
            return userCollectPO.getIsDel() == 0;
        }
        return false;
    }

    @Override
    public List<UserCollectPO> userCollectList(CollectBaseParam collectParam) {
        if (Objects.isNull(collectParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userId required");
        }
        return list(Wrappers.<UserCollectPO>lambdaQuery().eq(UserCollectPO::getUserId,collectParam.getUserId()).eq(UserCollectPO::getIsDel,false));
    }
}




