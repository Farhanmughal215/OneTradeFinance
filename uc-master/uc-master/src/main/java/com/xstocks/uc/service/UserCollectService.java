package com.xstocks.uc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.param.CollectBaseParam;
import com.xstocks.uc.pojo.po.UserCollectPO;

import java.util.List;

/**
* @author dongjunfu
* @description 针对表【user_collect(user collect)】的数据库操作Service
* @createDate 2023-11-14 15:55:46
*/
public interface UserCollectService extends IService<UserCollectPO> {

    boolean userCollect(CollectBaseParam collectParam);

    boolean isUserCollect(CollectBaseParam collectParam);

    List<UserCollectPO> userCollectList(CollectBaseParam collectParam);
}
