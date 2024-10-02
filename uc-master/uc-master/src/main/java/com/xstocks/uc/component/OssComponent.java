package com.xstocks.uc.component;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.xstocks.uc.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
public class OssComponent {

    @Autowired
    private AppConfig appConfig;

    private OSS ossClient;

    @PostConstruct
    void init() {
        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(appConfig.getAliyunConfig().getAk(), appConfig.getAliyunConfig().getSk());
        ossClient = new OSSClientBuilder().build("https://" + appConfig.getAliyunConfig().getOssConfig().getEndPoint(), credentialsProvider);
    }

    public String uploadInputStream(String objectName, InputStream is) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(appConfig.getAliyunConfig().getOssConfig().getBucket(), objectName, is);
        try {
            ossClient.putObject(putObjectRequest);
        } catch (Throwable t) {
            return StringUtils.EMPTY;
        }
        return "https://" + appConfig.getAliyunConfig().getOssConfig().getBucket() + "." + appConfig.getAliyunConfig().getOssConfig().getEndPoint() + "/" + objectName;
    }
}
