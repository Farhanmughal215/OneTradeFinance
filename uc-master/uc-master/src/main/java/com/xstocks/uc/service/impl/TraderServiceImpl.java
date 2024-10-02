package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.TraderMapper;
import com.xstocks.uc.pojo.Trader;
import com.xstocks.uc.pojo.TraderData;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.enums.UserStatusEnum;
import com.xstocks.uc.pojo.param.*;
import com.xstocks.uc.pojo.po.TraderWrapperPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.*;
import com.xstocks.uc.service.TraderDataService;
import com.xstocks.uc.service.TraderService;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Jiao交易员表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
@Slf4j
@Service
public class TraderServiceImpl extends ServiceImpl<TraderMapper, Trader> implements TraderService {

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TraderDataService traderDataService;

    @Value("${app.chainbridge.rpc.eth-address-url}")
    private String ethAddressUrl;

    @Value("${app.analysis.rpc.trader-stats-list-url}")
    private String traderListUrl;

    @Override
    @Transactional
    public void apply(ApplyTraderParam applyTraderParam, Long id) {
        UserPO userPO = userService.getById(id);
        if (null == userPO)
            throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR);

        if (userPO.getUserStatus() != UserStatusEnum.TRADABLE.getCode()
                && userPO.getUserStatus() != UserStatusEnum.FREEZE.getCode())
            throw new BizException(ErrorCode.USER_STATUS_NOT_ALLOW_ERROR);

        if (applyTraderParam.getSynopsis() != null && applyTraderParam.getSynopsis().length() > 300)
            throw new BizException(ErrorCode.SYNOPSIS_TOO_LONG_ERROR);

        if (applyTraderParam.getNickName() != null && applyTraderParam.getNickName().trim().length() > 64)
            throw new BizException(ErrorCode.ILLEGITMATE_NICKNAME_ERROR);

        Trader dbTrader = this.getOne(Wrappers.<Trader>lambdaQuery().eq(Trader::getUserId, id).last("LIMIT 1"));
        if (null != dbTrader && (dbTrader.getStatus() == 1 || dbTrader.getStatus() == 0))
            return;

        UserPO newUserPO = new UserPO();
        newUserPO.setId(id);
        newUserPO.setSynopsis(applyTraderParam.getSynopsis());
        newUserPO.setLogo(applyTraderParam.getLogo());
        newUserPO.setNickName(applyTraderParam.getNickName());
        userService.updateById(newUserPO);

        Trader trader = new Trader();
        trader.setUserId(id);
        trader.setStatus(0);
        trader.setMaxFollowCount(100);
        trader.setCreateTime(new Date());
        trader.setEmail(applyTraderParam.getEmail());
        trader.setProfitRatio(applyTraderParam.getProfitRatio());
        trader.setSocialAccount(applyTraderParam.getSocialAccount());
        trader.setContractCertificate(applyTraderParam.getContractCertificate());
        trader.setUpdateTime(new Date());
        if (dbTrader != null) {
            trader.setId(dbTrader.getId());
            trader.setUpdateTime(new Date());
            this.updateById(trader);
            return;
        }
        save(trader);
    }

    @Override
    @Transactional
    public void audit(AuditTraderParam auditTraderParam) {
        if (auditTraderParam.getStatus() != 1 && auditTraderParam.getStatus() != 2)
            throw new BizException(ErrorCode.PARAM_ERROR);

        Trader trader = getById(auditTraderParam.getId());
        if (Objects.isNull(trader))
            throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR);
        if (trader.getStatus() != 0)
            throw new BizException(ErrorCode.TRADER_ALREADY_AUDIT_ERROR);

        Trader newTrader = new Trader();
        newTrader.setId(trader.getId());
        newTrader.setStatus(auditTraderParam.getStatus());
        newTrader.setUpdateTime(new Date());
        newTrader.setAuditTime(new Date());
        if (auditTraderParam.getStatus() == 1) {
            newTrader.setAddressRandom(this.getBaseMapper().getNextAddressRandom());
            ObjectNode node = JsonUtil.createObjectNode();
            node.put("parameter", newTrader.getAddressRandom());
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
            BaseResp<LinkedHashMap> resp = restTemplate.postForEntity(ethAddressUrl, httpEntity, BaseResp.class).getBody();
            if (resp == null || resp.getCode() != 0) {
                throw new BizException(ErrorCode.TRADER_AUDIT_ERROR);
            }
            newTrader.setAddress(resp.getData().getOrDefault("address", "").toString());
            newTrader.setPath(resp.getData().getOrDefault("path", "").toString());
            saveTraderData(trader.getId(), trader.getUserId().intValue());
        } else {
            log.info("审核交易员拒绝:{}", auditTraderParam);
            newTrader.setStatus(2);
        }
        updateById(newTrader);
    }

    private void saveTraderData(Integer traderId, Integer userId) {
        if (traderDataService.exists(Wrappers.<TraderData>lambdaQuery().eq(TraderData::getTraderId, traderId)))
            return;
        TraderData data = new TraderData();
        data.setTraderId(traderId);
        data.setUserId(userId);
        data.setFollowCount(0);
        data.setTradeCount(0);
        data.setPlAmount(BigDecimal.ZERO);
        data.setTotalAmount(BigDecimal.ZERO);
        data.setFollowTotalAmount(BigDecimal.ZERO);
        data.setFollowPl(BigDecimal.ZERO);
        data.setTotalProfit(BigDecimal.ZERO);
        traderDataService.save(data);
    }

    @Override
    public Object traderList(TraderQueryParam queryParam, Long id) {
        ObjectNode obj = JsonUtil.createObjectNode();
        log.info("queryParam:{}", queryParam);
        obj.put("assetsMax", queryParam.getAssetsMax());
        obj.put("assetsMin", queryParam.getAssetsMin());
        obj.put("leadingScaleMax", queryParam.getLeadingScaleMax());
        obj.put("leadingScaleMin", queryParam.getLeadingScaleMin());
        obj.put("winRate", queryParam.getWinRate());
        obj.put("leadingDays", queryParam.getLeadingDays());

        obj.put("sort", queryParam.getSort());
        obj.put("pageNum", queryParam.getPageNum());
        obj.put("pageSize", queryParam.getPageSize());
        //0：满员,1：不满员
        obj.put("full", queryParam.getFull());
        obj.put("fullTraderIds", this.getBaseMapper().getFullList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        obj.put("availableIds", this.getBaseMapper().getAvailableList().stream().map(String::valueOf).collect(Collectors.joining(",")));

        log.info("交易员筛选条件:{}", JsonUtil.toJSONString(obj));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> request = new HttpEntity<>(obj, headers);
        BaseResp<LinkedHashMap> result = restTemplate.postForEntity(traderListUrl, request, BaseResp.class).getBody();
        if (result == null || result.getCode() != 0) {
            return null;
        }

        LinkedHashMap json = result.getData();
        if (!json.containsKey("records"))
            return result;
        List<LinkedHashMap> array = (List<LinkedHashMap>) json.get("records");
        List<Integer> uidList = new ArrayList<>();
        for (LinkedHashMap map : array) {
            if (!map.containsKey("uid"))
                continue;
            uidList.add(map.get("uid") == null ? 0 : Integer.parseInt(map.get("uid").toString()));
        }
        if (uidList.isEmpty())
            return result;
        List<TraderWrapperPO> poList = getBaseMapper().getTraderInfo(uidList, id);
        for (LinkedHashMap map : array) {
            if (!map.containsKey("uid"))
                continue;
            Integer uid = Integer.parseInt(map.get("uid").toString());
            TraderWrapperPO po = poList.stream().filter(p -> p.getUserId().equals(uid)).findFirst().orElse(null);
            if (po == null)
                continue;
            map.put("nickName", po.getNickName());
            map.put("currentFollowCount", po.getCurrentFollowCount());
            map.put("logo", po.getLogo());
            map.put("maxFollowCount", po.getMaxFollowCount());
            map.put("followStatus", po.getFollowStatus());
            //0：满员,1：不满员
            map.put("full", po.getCurrentFollowCount() >= po.getMaxFollowCount() ? 0 : 1);
        }
        if ("follower_count".equals(queryParam.getSort()))
            //根据currentFollowCount倒序排序
            array.sort((o1, o2) -> Integer.parseInt(o2.get("currentFollowCount").toString()) - Integer.parseInt(o1.get("currentFollowCount").toString()));
        return result;
    }

    @Override
    public IPage<TraderStatisticsVo> bTraderList(BQueryTraderParam queryTraderParam) {
        if (null == queryTraderParam) {
            queryTraderParam = new BQueryTraderParam();
            queryTraderParam.setPageNo(1L);
            queryTraderParam.setPageSize(10L);
        }
        if (queryTraderParam.getPageNo() == null)
            queryTraderParam.setPageNo(1L);

        if (queryTraderParam.getPageSize() == null)
            queryTraderParam.setPageSize(10L);

        Page<Trader> page = new Page<>(queryTraderParam.getPageNo(), queryTraderParam.getPageSize());
        return this.getBaseMapper().bTraderList(page, queryTraderParam);
    }

    @Override
    public void freeze(AuditTraderParam auditTraderParam) {
        if (auditTraderParam.getStatus() != 1 && auditTraderParam.getStatus() != 3)
            throw new BizException(ErrorCode.PARAM_ERROR);

        Trader trader = getById(auditTraderParam.getId());
        if (Objects.isNull(trader))
            throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR);

        Trader newTrader = new Trader();
        newTrader.setId(trader.getId());
        newTrader.setStatus(auditTraderParam.getStatus());
        newTrader.setUpdateTime(new Date());
        updateById(newTrader);
    }

    @Override
    public TraderStatisticsVo queryTraderDetail(BTraderDetailParam detailParam) {
        return this.getBaseMapper().queryTraderDetail(detailParam);
    }


    @Override
    public IPage<BTraderListVo> waitList(BQueryTraderParam queryTraderParam) {
        if (null == queryTraderParam) {
            queryTraderParam = new BQueryTraderParam();
            queryTraderParam.setPageNo(1L);
            queryTraderParam.setPageSize(10L);
        }

        if (queryTraderParam.getPageNo() == null)
            queryTraderParam.setPageNo(1L);

        if (queryTraderParam.getPageSize() == null)
            queryTraderParam.setPageSize(10L);

        queryTraderParam.setStatus(0);
        Page<Trader> page = new Page<>(queryTraderParam.getPageNo(), queryTraderParam.getPageSize());
//        return page(page, wrapper);
        return this.getBaseMapper().waitList(page, queryTraderParam);
    }

    @Override
    public void updateTrader(UpdateTraderParam updateTraderParam, Long id) {
        if (updateTraderParam == null)
            return;
        if (updateTraderParam.getProfitRatio() == null)
            return;

        Trader trader = getByUserId(id);
        if (Objects.isNull(trader))
            throw new BizException(ErrorCode.TRADER_NOT_EXIST_ERROR);
        Trader newTrader = new Trader();
        newTrader.setId(trader.getId());
        newTrader.setProfitRatio(updateTraderParam.getProfitRatio());
        newTrader.setUpdateTime(new Date());
        updateById(newTrader);
    }

    @Override
    public Trader getTraderByAddress(String address) {
        return this.getBaseMapper().getTraderByAddress(address);
    }

    @Override
    public Boolean checkUserIdTrader(Integer userId) {
        return this.getBaseMapper().checkUserIdTrader(userId) > 0;
    }

    @Override
    public Trader getByUserId(Long userId) {
        return this.getOne(Wrappers.<Trader>lambdaQuery()
                .eq(Trader::getUserId, userId)
                .eq(Trader::getStatus, 1)
                .last("LIMIT 1"));
    }

    @Override
    public void addBalance(Integer id, String amount) {
        Trader trader = getById(id);
        //此处不可能为空
        Trader newTrader = new Trader();
        newTrader.setId(trader.getId());
        newTrader.setAssets(trader.getAssets().add(new BigDecimal(amount)));
        updateById(newTrader);
    }

    @Override
    public TraderUserVo getTraderUser(Long userId) {
        Trader trader = this.getOne(Wrappers.<Trader>lambdaQuery()
                .eq(Trader::getUserId, userId)
                .last("LIMIT 1"));
        TraderUserVo vo = new TraderUserVo();
        if (null != trader) {
            BeanUtils.copyProperties(trader, vo);
            vo.setId(trader.getId());
            vo.setUserId(trader.getUserId());
            vo.setStatus(trader.getStatus());
        }
        UserPO userPO = userService.getById(userId);
        vo.setNickName(userPO.getNickName());
        vo.setLogo(userPO.getLogo());
        vo.setSynopsis(userPO.getSynopsis());
        vo.setUserId(userPO.getId());
        vo.setUserAddress(userPO.getTxAddress());
        return vo;
    }

    @Override
    public TraderUserVo getTraderData(Long traderUid, Long currentUid) {
        Trader trader = this.getOne(Wrappers.<Trader>lambdaQuery()
                .eq(Trader::getUserId, traderUid)
                .eq(Trader::getStatus, 1)
                .last("LIMIT 1"));
        if (Objects.isNull(trader))
            throw new BizException(ErrorCode.TRADER_NOT_EXIST_ERROR);
        TraderUserVo vo = new TraderUserVo();
        BeanUtils.copyProperties(trader, vo);
        vo.setId(trader.getId());
        vo.setUserId(trader.getUserId());

        UserPO userPO = userService.getById(traderUid);
        vo.setNickName(userPO.getNickName());
        vo.setLogo(userPO.getLogo());
        vo.setSynopsis(userPO.getSynopsis());
        vo.setUserId(userPO.getId());
        vo.setFollowStatus(getBaseMapper().getFollowStatus(traderUid, currentUid));
        return vo;
    }

    @Override
    public TraderInfoDataVo getTraderInfo(Long userId) {
        log.info("查询交易员数据userId:{}", userId);
        return getBaseMapper().getTraderDataInfo(userId);
    }


    @Override
    public FollowSetVo getFollowSet(Long traderUid, Long currentUid) {
        return getBaseMapper().getFollowSet(traderUid, currentUid);
    }

}
