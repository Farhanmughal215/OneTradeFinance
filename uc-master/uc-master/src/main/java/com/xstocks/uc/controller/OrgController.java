package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.CreateOrgParam;
import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.OrgNodeVO;
import com.xstocks.uc.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @ClassName OrgController
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/21 17:09
 **/

@Slf4j
@RestController
public class OrgController {

    @Autowired
    private OrgService orgService;

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')||hasRole('ROLE_OPERATOR')")
    @PostMapping(value = "/b/org/my")
    public BaseResp<OrgNodeVO> myOrg(
            @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
            UserPO currentLoginUser) {
        OrgNodeVO orgNodeVO = orgService.getOrgTreeOfCurrentLoginUser(currentLoginUser);
        return BaseResp.success(orgNodeVO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/org/create")
    public BaseResp<Boolean> createOrg(@RequestBody CreateOrgParam createOrgParam,
                                       @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                       UserPO currentLoginUser) {
        if (StringUtils.isBlank(createOrgParam.getCode())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "code required");
        }
        if (StringUtils.isBlank(createOrgParam.getName())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "name required");
        }
        if (StringUtils.isBlank(createOrgParam.getParentOrgCode())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "parentOrgCode required");
        }
        OrgPO parentOrg = orgService.getOne(
                Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, createOrgParam.getParentOrgCode().trim())
                        .last("LIMIT 1"));
        if (Objects.isNull(parentOrg)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding org");
        }
        OrgPO currentUserOrg = orgService.getOne(
                Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentLoginUser.getOrgCode()).last("LIMIT 1"));

        if (!parentOrg.getOrgCodePath().startsWith(currentUserOrg.getOrgCodePath())) {
            throw new BizException(ErrorCode.UNAUTHORIZED,
                    "no auth to mange" + createOrgParam.getParentOrgCode().trim());
        }

        OrgPO org = getOrgPO(createOrgParam, parentOrg, currentUserOrg);
        orgService.save(org);
        return BaseResp.success(true);
    }

    private OrgPO getOrgPO(CreateOrgParam createOrgParam, OrgPO parentOrg, OrgPO currentUserOrg) {
        OrgPO org = new OrgPO();
        org.setCode(createOrgParam.getCode().trim());
        org.setName(createOrgParam.getName().trim());
        org.setLevel(parentOrg.getLevel() + 1);
        org.setOrgCodePath(parentOrg.getOrgCodePath() + "/" + createOrgParam.getCode().trim());
        org.setOrgNamePath(parentOrg.getOrgNamePath() + "/" + createOrgParam.getName().trim());
        org.setParentOrgCode(parentOrg.getCode());
        org.setAddBy(currentUserOrg.getId());
        org.setUpdateBy(currentUserOrg.getId());
        return org;
    }
}
