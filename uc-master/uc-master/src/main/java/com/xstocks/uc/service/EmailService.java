package com.xstocks.uc.service;


import com.xstocks.uc.config.domain.EmailEntity;

import java.util.Map;

public interface EmailService {

    void sendEmailHtml(Map<String, String> map);


    void sendHtmlEmail(EmailEntity entity);

    Boolean sendEmailVerify(String email);
}
