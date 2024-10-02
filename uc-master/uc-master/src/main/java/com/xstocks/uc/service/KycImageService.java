package com.xstocks.uc.service;

import com.xstocks.uc.pojo.KycImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Kyc图片表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-01-12
 */
public interface KycImageService extends IService<KycImage> {

    void removeByKycId(Long kycId);

    List<String> listByKycId(Long id);
}
