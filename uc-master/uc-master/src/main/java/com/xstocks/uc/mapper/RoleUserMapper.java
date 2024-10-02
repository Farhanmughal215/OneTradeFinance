package com.xstocks.uc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.uc.pojo.po.RoleUserPO;
import org.springframework.stereotype.Repository;

/**
* @author firtuss
* @description 针对表【role_user(RBAC role user relation)】的数据库操作Mapper
* @createDate 2023-09-03 20:55:22
* @Entity generator.domain.RoleUser
*/
@Repository
public interface RoleUserMapper extends BaseMapper<RoleUserPO> {

}




