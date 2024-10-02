package com.xstocks.uc.controller;

import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.remote.PolygoWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName WsController
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/10 19:04
 **/

@Slf4j
@Controller
public class WsController {

    @Autowired
    private PolygoWsService polygoWsService;

    @GetMapping("/a/ws")
    public String ws() {
        return "ws";
    }

    @GetMapping("/a/ws/health")
    @ResponseBody
    public BaseResp<Boolean> getWsHealth() {
        return BaseResp.success(polygoWsService.getIsSubscribing());
    }

    @GetMapping("/a/ws/resub")
    @ResponseBody
    public BaseResp<Boolean> resub() {
        Boolean isSubscribing = polygoWsService.getIsSubscribing();
        if (!isSubscribing) {
            polygoWsService.subscribe();
        }
        return BaseResp.success(true);
    }
}
