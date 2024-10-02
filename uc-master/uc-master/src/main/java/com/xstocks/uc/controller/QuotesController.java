//package com.xstocks.uc.controller;
//
//import com.xstocks.uc.exception.BizException;
//import com.xstocks.uc.pojo.dto.polygo.AggregatesDTO;
//import com.xstocks.uc.pojo.dto.polygo.TickerLatestStateDTO;
//import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
//import com.xstocks.uc.pojo.enums.ErrorCode;
//import com.xstocks.uc.pojo.param.QuoteQueryParam;
//import com.xstocks.uc.pojo.param.polygo.AggregatesQueryParam;
//import com.xstocks.uc.pojo.param.remote.TickerIdParam;
//import com.xstocks.uc.pojo.vo.BaseResp;
//import com.xstocks.uc.pojo.vo.Slice;
//import com.xstocks.uc.service.remote.PolygoService;
//import com.xstocks.uc.utils.LocalCacheUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//import static com.xstocks.uc.pojo.constants.CommonConstant.LOCAL_CACHE_ALL_TICKER;
//
//@Slf4j
//@RestController
//public class QuotesController {
//
//    @Autowired
//    private PolygoService polygoService;
//
//    @PostMapping("/a/agg/l")
//    public BaseResp<Slice<AggregatesDTO>> getAggregates(@RequestBody AggregatesQueryParam queryParam) {
//        return BaseResp.success(polygoService.getAggregates(queryParam));
//    }
//
//    @PostMapping("/a/quote/l")
//    public BaseResp<Map<Long, TickerLatestStateDTO>> listTickersState(@RequestBody QuoteQueryParam queryParam) {
//        return BaseResp.success(polygoService.getLatestStateTickers(queryParam.getTickerIds()));
//    }
//
//    @PostMapping("/a/quote/r")
//    public BaseResp<TickerLatestStateDTO> readTickerState(@RequestBody TickerIdParam tickerIdParam) {
//        Map<String, TickerAbbreviationDTO> tickerAbbreviationDTOMap = LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER).get(LOCAL_CACHE_ALL_TICKER);
//        TickerAbbreviationDTO tickerAbbreviationDTO = tickerAbbreviationDTOMap.values().stream().filter(x -> x.getId().equals(tickerIdParam.getId())).findFirst().orElseThrow(() -> new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding ticker"));
//        return BaseResp.success(polygoService.getLatestStateTicker(tickerAbbreviationDTO.getId(), tickerAbbreviationDTO.getSymbol()));
//    }
//
//
//}
