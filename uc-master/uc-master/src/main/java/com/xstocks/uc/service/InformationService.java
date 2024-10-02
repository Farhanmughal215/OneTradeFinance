package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xstocks.uc.pojo.Information;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.param.NewsQueryParam;
import com.xstocks.uc.pojo.vo.BaseResp;

/**
 * <p>
 * 资讯表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-01-14
 */
public interface InformationService extends IService<Information> {

    BaseResp<IPage<Information>> queryInformation(NewsQueryParam newsQueryParam);

    void delete(Integer type,String lang);
}
