package com.xstocks.uc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OrgNodeVO;

/**
* @author dongjunfu
* @description 针对表【org(org)】的数据库操作Service
* @createDate 2023-10-28 14:13:13
*/
public interface OrgService extends IService<OrgPO> {
    OrgNodeVO getOrgTreeOfCurrentLoginUser(UserPO currentLoginUser);
    OrgNodeVO orgRoot(OrgNodeVO currentUserOrgNodeVO);

}
