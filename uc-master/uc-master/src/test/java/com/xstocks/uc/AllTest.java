package com.xstocks.uc;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Splitter;
import com.xstocks.uc.config.domain.EmailEntity;
import com.xstocks.uc.config.security.JwtManager;
import com.xstocks.uc.mapper.OrgMapper;
import com.xstocks.uc.pojo.dto.order.OrderUserBalance;
import com.xstocks.uc.pojo.dto.polygo.AggregatesDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
import com.xstocks.uc.pojo.param.polygo.AggregatesQueryParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OrgNodeVO;
import com.xstocks.uc.pojo.vo.Slice;
import com.xstocks.uc.pojo.vo.UserVO;
import com.xstocks.uc.service.EmailService;
import com.xstocks.uc.service.OrgService;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.service.remote.OrderService;
import com.xstocks.uc.service.remote.PolygoService;
import com.xstocks.uc.service.remote.TickerService;
import com.xstocks.uc.task.RefreshTickerStateDailyTask;
import com.xstocks.uc.utils.IdHelper;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;

import static com.xstocks.uc.pojo.constants.CommonConstant.EMAIL_PATTERN;
import static com.xstocks.uc.pojo.constants.CommonConstant.PHONE_PATTERN;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "debug")
public class AllTest {

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private PolygoService polygoService;

    @Autowired
    private TickerService tickerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private RefreshTickerStateDailyTask refreshTickerStateDailyTask;

    @Autowired
    private OrgService orgService;

    @Autowired
    private EmailService emailService;


    @Test
    public void testEmail() {
        EmailEntity entity = new EmailEntity();
        entity.setContent("你好,你的验证码是:123456,有效期为10分钟");
        entity.setTos("1283277297@qq.com");
        entity.setSubject("欢迎注册DEX，请查收验证码！");
        emailService.sendHtmlEmail(entity);
    }

    @Test
    public void testOrgNode() {
//        List<OrgNodeVO> orgNodeVOList = orgService.getNLevelOrgNode(0);
//        log.info(JsonUtil.toJSONString(orgNodeVOList));
//        for (OrgNodeVO o : orgNodeVOList) {
//            List<OrgNodeVO> orgNodeVOList1 = orgService.getOrgChild(o.getCode());
//            log.info(JsonUtil.toJSONString(orgNodeVOList1));
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("inputCode", "111");
        OrgNodeVO orgNodeVO = orgMapper.getOrgNode(map);

//        OrgNodeVO root = orgService.orgRoot(orgNodeVOList.get(0));
        log.info(JsonUtil.toJSONString(orgNodeVO));

        Assert.isTrue("/xteam".startsWith("/xteam"), "no /xteam not start with /xteam");
    }

    @Test
    public void testJwt() {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        String token = jwtManager.generate("system:system", Optional.empty());
        log.info(token);
        DecodedJWT claims = jwtManager.parse(token);
        log.info(claims.toString());
    }

    @Test
    public void testUtil() {
        int i = 0;
        while (i < 5) {
            log.info(IdHelper.generateId());
            i++;
        }

        String nowStr = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        log.info(nowStr);
    }

    @Test
    public void testPolygo() {
        AggregatesQueryParam aggregatesQueryParam = new AggregatesQueryParam();
        aggregatesQueryParam.setTickerId(1161L)
                .setMultiplier(1)
                .setTimeSpan("minute")
                .setFrom("1684800000000")
                .setTo("1700438399999")
                .setPageSize(300).setSort("desc");

        int pages = 1;
        int dataCount = 0;
        Slice<AggregatesDTO> slice = polygoService.getAggregates(aggregatesQueryParam);
        dataCount += slice.getData().size();
        log.info("第" + (pages++) + "页");
        while (slice.hasNextSlice()) {
            //设置下一页请求参数
            aggregatesQueryParam.setNextPageToken(slice.getNextPageToken());
            slice = polygoService.getAggregates(aggregatesQueryParam);
            log.info("第" + (pages++) + "页");
            dataCount += slice.getData().size();
        }
        log.info("总数据条数：{}", dataCount);
    }

    @Test
    public void testTicker() {
//        TickerDetailDTO tickerDetailDTO = tickerService.getTickerDetailById(53L);
//        log.info(JsonUtil.toJSONString(tickerDetailDTO));

        List<TickerAbbreviationDTO> tickerAbbreviationDTOList = tickerService.getAllTickers();
        log.info(JsonUtil.toJSONString(tickerAbbreviationDTOList));

    }

    @Test
    public void testOrder() {
        UserPO userPO = userService.getById(13);
        UserVO userVO = MapperUtil.INSTANCE.userPO2VO(userPO);
        OrderUserBalance userBalance = orderService.getOrderBalance(userVO);
        log.info(JsonUtil.toJSONString(userBalance));
    }

    @Test
    public void testRefreshTickerState() {
        refreshTickerStateDailyTask.refreshTickerState();
    }

    @Test
    public void guavaTest() {
        List<String> orgCodeList = Splitter.on("/").omitEmptyStrings().splitToList("/xteam");
        log.info(JsonUtil.toJSONString(orgCodeList));
    }

    @Test
    public void regexTest() {
        Matcher phoneMatcher = PHONE_PATTERN.matcher("+8618801170768");
        log.info("+8618801170768 " + phoneMatcher.matches());

        Matcher emailMatcher = EMAIL_PATTERN.matcher("afu031@gmail.com");
        log.info("afu031@gmail.com " + emailMatcher.matches());
    }

}
