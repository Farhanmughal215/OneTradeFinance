package com.xstocks.uc.controller;

import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.dto.polygo.TickerLatestStateDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerDetailDTO;
import com.xstocks.uc.pojo.param.CollectBaseParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.TickerVO;
import com.xstocks.uc.service.UserCollectService;
import com.xstocks.uc.service.remote.PolygoService;
import com.xstocks.uc.service.remote.TickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName CollectController
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/14 17:08
 **/

@Slf4j
@RestController
public class CollectController {

    @Autowired
    private UserCollectService userCollectService;

    @Autowired
    private TickerService tickerService;

    @Autowired
    private PolygoService polygoService;


    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/collect/u")
    public BaseResp<Boolean> userCollectTicker(@RequestBody CollectBaseParam collectParam,
                                               @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                               UserPO currentLoginUser) {
        collectParam.setUserId(currentLoginUser.getId());
        return BaseResp.success(userCollectService.userCollect(collectParam));
    }


    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/collect/r")
    public BaseResp<Boolean> readUserCollectTicker(@RequestBody CollectBaseParam collectParam,
                                                   @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                   UserPO currentLoginUser) {
        collectParam.setUserId(currentLoginUser.getId());
        return BaseResp.success(userCollectService.isUserCollect(collectParam));
    }


    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/collect/l")
    public BaseResp<List<TickerVO>> userCollectTickers(@RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                                       UserPO currentLoginUser) {
        CollectBaseParam collectBaseParam = new CollectBaseParam();
        collectBaseParam.setUserId(currentLoginUser.getId());

        List<TickerVO> list =
                userCollectService.userCollectList(collectBaseParam).stream()
                        .map(x -> getTickerVO(x.getTickerId())).collect(
                                Collectors.toList());

        return BaseResp.success(list);
    }

    private TickerVO getTickerVO(Long id) {
        TickerDetailDTO tickerDetailDTO = tickerService.getTickerDetailById(id);
        TickerVO tickerVO = new TickerVO();
        tickerVO.setTickerInfo(tickerDetailDTO);
        if (Objects.nonNull(tickerDetailDTO)) {
            TickerLatestStateDTO tickerLatestStateDTO = polygoService.getLatestStateTicker(tickerDetailDTO.getId(),
                    tickerDetailDTO.getSymbol());
            tickerVO.setTickerState(tickerLatestStateDTO);
        }
        tickerVO.setIsCollected(true);
        return tickerVO;
    }
}
