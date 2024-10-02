package com.xstocks.uc.service.remote;


import com.xstocks.uc.config.AppConfig;
import com.zerobounce.ZBValidateStatus;
import com.zerobounce.ZeroBounceSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class ZeroBounceService {

    @Autowired
    private AppConfig appConfig;

    @PostConstruct
    void init() {
        ZeroBounceSDK.getInstance().initialize(appConfig.getZeroBounceConfig().getApiKey());
    }

    public boolean validateEmail(String email, String ip) {
        AtomicBoolean result = new AtomicBoolean(true);
        ZeroBounceSDK.getInstance().validate(
                email,
                ip,
                response -> {
                    if (ZBValidateStatus.VALID != response.getStatus()) {
                        result.set(false);
                    }
                },
                errorResponse -> {
                    result.set(false);
                });
        return result.get();
    }

}
