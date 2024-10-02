package com.xstocks.uc.controller;

import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.param.RecordQueryParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.TradeRecordVo;
import com.xstocks.uc.service.TradeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 资金划转表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-03-20
 */
@RestController
public class TradeRecordController {

    @Autowired
    private TradeRecordService tradeRecordService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/record/list")
    public BaseResp<List<TradeRecordVo>> listRecord(@RequestBody RecordQueryParam recordQueryParam,
                                                    @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                    UserPO currentLoginUser) {
        if (recordQueryParam == null) {
            recordQueryParam = new RecordQueryParam();
        }
        recordQueryParam.setUserId(currentLoginUser.getId());
        return BaseResp.success(tradeRecordService.queryRecordPage(recordQueryParam));
    }

}
