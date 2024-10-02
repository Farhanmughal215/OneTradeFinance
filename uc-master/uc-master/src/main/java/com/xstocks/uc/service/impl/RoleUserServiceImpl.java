package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.RoleUserMapper;
import com.xstocks.uc.pojo.po.RoleUserPO;
import com.xstocks.uc.service.RoleUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author firtuss
* @description 针对表【role_user(RBAC role user relation)】的数据库操作Service实现
* @createDate 2023-09-03 20:55:22
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUserPO>
        implements RoleUserService {

}




