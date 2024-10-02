package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Splitter;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.OrgMapper;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OrgNodeVO;
import com.xstocks.uc.service.OrgService;
import com.xstocks.uc.utils.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dongjunfu
 * @description 针对表【org(org)】的数据库操作Service实现
 * @createDate 2023-10-28 14:13:13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrgServiceImpl extends ServiceImpl<OrgMapper, OrgPO>
        implements OrgService {

    @Autowired
    private OrgMapper orgMapper;

    @Override
    public OrgNodeVO getOrgTreeOfCurrentLoginUser(UserPO currentLoginUser) {
        Map<String, Object> map = new HashMap<>();
        map.put("inputCode", currentLoginUser.getOrgCode());
        OrgNodeVO currentUserOrgNodeVO = orgMapper.getOrgNode(map);

        if (Objects.isNull(currentUserOrgNodeVO)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding org of current user");
        }

        return currentUserOrgNodeVO;
    }


    @Override
    public OrgNodeVO orgRoot(OrgNodeVO currentUserOrgNodeVO) {
        OrgNodeVO parent = MapperUtil.INSTANCE.cloneOrgNodeVO(currentUserOrgNodeVO.getParent());
        parent.setChildren(Collections.singletonList(currentUserOrgNodeVO));
        String parentOrgCode = parent.getParentOrgCode();
        while (StringUtils.isNotBlank(parentOrgCode)) {
            Map<String, Object> param = new HashMap<>();
            param.put("parent_org_code", parentOrgCode);
            OrgNodeVO orgNodeVO = orgMapper.getParentOrg(param);
            orgNodeVO.setChildren(Collections.singletonList(parent));
            parent = orgNodeVO;
            parentOrgCode = parent.getParentOrgCode();
        }
        return parent;
    }
}




