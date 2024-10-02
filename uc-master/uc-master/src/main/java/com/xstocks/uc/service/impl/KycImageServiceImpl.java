package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.KycImageMapper;
import com.xstocks.uc.pojo.KycImage;
import com.xstocks.uc.service.KycImageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Kyc图片表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-01-12
 */
@Service
public class KycImageServiceImpl extends ServiceImpl<KycImageMapper, KycImage> implements KycImageService {

    @Override
    public void removeByKycId(Long kycId) {
        remove(Wrappers.<KycImage>lambdaQuery().eq(KycImage::getKycId, kycId));
    }

    @Override
    public List<String> listByKycId(Long id) {
        return list(Wrappers.<KycImage>lambdaQuery().eq(KycImage::getKycId, id).select(KycImage::getImageUrl)).stream().map(KycImage::getImageUrl).collect(Collectors.toList());
    }
}
