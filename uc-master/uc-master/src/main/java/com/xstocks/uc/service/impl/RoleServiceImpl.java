package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.RoleMapper;
import com.xstocks.uc.pojo.po.RolePO;
import com.xstocks.uc.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author firtuss
* @description 针对表【role(RBAC role)】的数据库操作Service实现
* @createDate 2023-09-03 20:49:23
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RolePO>
    implements RoleService {

}




