package com.xstocks.uc.pojo.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName UploadParam
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/24 17:37
 **/
@Data
public class UploadParam {
    private MultipartFile file;
}
