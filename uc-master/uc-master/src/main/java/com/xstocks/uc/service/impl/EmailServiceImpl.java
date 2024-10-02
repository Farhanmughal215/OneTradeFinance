package com.xstocks.uc.service.impl;

import com.xstocks.uc.common.Constants;
import com.xstocks.uc.config.domain.EmailEntity;
import com.xstocks.uc.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author kevin
 * @description: 发送邮件服务
 * @date 2024/01/09 11:48
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.mail.username}")
    private String from;

    @Resource(name = "eventHandleExecutor")
    private Executor eventHandleExecutor;

    @Override
    public void sendEmailHtml(Map<String, String> map) {
        //收件邮箱，
        String content = map.get("∆content");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }

        EmailEntity entity = new EmailEntity();
        entity.setSubject(map.get("∆title"));
        entity.setTos(map.get("∆to"));
        entity.setContent(content);
        if(map.containsKey("∆ccEmail")&& StringUtils.isNotEmpty("∆ccEmail")){
            entity.setCcEmail(map.get("∆ccEmail"));
        }
        this.sendHtmlEmail(entity);
    }

    @Override
    public void sendHtmlEmail(EmailEntity entity) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            /*发送者邮箱，即为开通了smtp服务的邮箱*/
            String nickname = "One Trade Team";
            if (null != entity.getCcEmail())
                helper.setCc(entity.getCcEmail());

            helper.setFrom(nickname + '<' + from + '>');
            /*发送到的邮箱*/
            helper.setTo(entity.getTos().split(","));
//            helper.setTo(entity.getTos());
            helper.setSubject(entity.getSubject());
            entity.setContent(entity.getContent());
            helper.setText(entity.getContent(), true);
            eventHandleExecutor.execute(() -> mailSender.send(message));
            log.info("发送HTML邮件成功:{}", entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Boolean sendEmailVerify(String email) {
        int num = (int) ((Math.random() * 9 + 1) * 100000);
        String verifyCode = num + StringUtils.EMPTY;
        redisTemplate.opsForValue().set(Constants.VERIFY_CODE_PREFIX + email, verifyCode, 121L, TimeUnit.SECONDS);
        String content = "<style>\n" +
                "  body {\n" +
                "    font-family: Arial, sans-serif;\n" +
                "  }\n" +
                "\n" +
                "  h2 {\n" +
                "    color: #333;\n" +
                "  }\n" +
                "\n" +
                "  p {\n" +
                "    margin-bottom: 10px;\n" +
                "  }\n" +
                "\n" +
                "  strong {\n" +
                "    color: #000;\n" +
                "    background-color: #f1f1f1;\n" +
                "    padding: 3px 5px;\n" +
                "    border-radius: 3px;\n" +
                "  }\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h2>Dear user,</h2>\n" +
                "<p>Your verify code is : <strong>[" + verifyCode +"]</strong>.</p>\n" +
                "<p>Thank you for your support!</p>\n" +
                "[One Trade Team]</p>\n" +
                "</body>";
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setContent(content);
        emailEntity.setTos(email);
        emailEntity.setSubject("Welcome register One Trade Account");
        this.sendHtmlEmail(emailEntity);
        return Boolean.TRUE;
    }

}
