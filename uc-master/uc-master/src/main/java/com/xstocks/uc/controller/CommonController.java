package com.xstocks.uc.controller;

import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.component.OssComponent;
import com.xstocks.uc.exception.BizException;
import static com.xstocks.uc.pojo.constants.CommonConstant.CURRENT_LOGIN_USER;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.UploadParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @ClassName CommonController
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/24 17:32
 **/
@Slf4j
@RestController
public class CommonController {

    @Autowired
    private OssComponent ossComponent;

    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')||hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = {"/c/common/upload", "/b/common/upload"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResp<String> upload(UploadParam uploadParam,
                                   @RequestAttribute(CURRENT_LOGIN_USER)
                                   UserPO currentLoginUser) {
        if (Objects.isNull(uploadParam.getFile())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "file required");
        }
        MultipartFile file = uploadParam.getFile();
        String currentMillsTimeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String objectName = currentLoginUser.getBizId() + "/" + currentMillsTimeStamp + "/" + file.getOriginalFilename();
        String uploadResult;
        try {
            uploadResult = ossComponent.uploadInputStream(objectName, file.getInputStream());
            if (StringUtils.isEmpty(uploadResult)) {
                throw new BizException(ErrorCode.TOO_MANY_REQUESTS);
            }
        } catch (IOException e) {
            log.error("upload_ex", e);
            throw new BizException(ErrorCode.TOO_MANY_REQUESTS);
        }
        return BaseResp.success(uploadResult);
    }

}
