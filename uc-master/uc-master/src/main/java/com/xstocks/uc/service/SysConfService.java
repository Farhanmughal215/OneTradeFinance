package com.xstocks.uc.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.po.SysConfPO;
import com.xstocks.uc.pojo.po.UserPO;

import java.util.Map;
import java.util.Set;

/**
 * @author firtuss
 * @description 针对表【sys_conf(system configuration)】的数据库操作Service
 * @createDate 2023-09-05 17:07:07
 */
public interface SysConfService extends IService<SysConfPO> {
    Map<String, String> getAllBizConfig();

    void updateBizConfig(Map<String, String> map, UserPO updateBy);

    void loadBizConfig(Set<String> keys);

    void delBizConfig(Set<String> keys);

    String getBizConfig(String key);
}
