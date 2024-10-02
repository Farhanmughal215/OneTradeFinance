package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.InformationMapper;
import com.xstocks.uc.pojo.Information;
import com.xstocks.uc.pojo.param.NewsQueryParam;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.InformationService;
import com.xstocks.uc.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 资讯表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-01-14
 */
@Service
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information> implements InformationService {

    @Override
    public BaseResp<IPage<Information>> queryInformation(NewsQueryParam newsQueryParam) {
        Page<Information> page = new Page<>(newsQueryParam.getPageNo(), newsQueryParam.getPageSize());
        LambdaQueryWrapper<Information> wrapper = Wrappers.lambdaQuery();
        /*
        wrapper.eq(Information::getType, newsQueryParam.getNewsType());
        if (newsQueryParam.getNewsType() == 2) {
            wrapper.eq(Information::getLang, "global");
        }else {
            if (Objects.nonNull(newsQueryParam.getLang())) {
                wrapper.eq(Information::getLang, newsQueryParam.getLang());
            }
        }

*/
        if (Objects.nonNull(newsQueryParam.getDate())) {
            if ("today".equalsIgnoreCase(newsQueryParam.getDate())) {
                //查询今天
                wrapper.ge(Information::getCreateTime, DateUtils.formatDateToMin(new Date()));
                wrapper.le(Information::getCreateTime, new Date());
            } else if ("week".equalsIgnoreCase(newsQueryParam.getDate())) {
                //查询本周
                wrapper.ge(Information::getCreateTime, DateUtils.formatDateToMin(DateUtils.getWeekStartDay()));
                wrapper.le(Information::getCreateTime, new Date());
            }
        }
        IPage<Information> pageResult = this.page(page, wrapper);
        return BaseResp.success(pageResult);
    }

    @Override
    public void delete(Integer type, String lang) {
        LambdaQueryWrapper<Information> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Information::getType, type);
        wrapper.eq(Information::getLang, lang);
        this.remove(wrapper);
    }
}
