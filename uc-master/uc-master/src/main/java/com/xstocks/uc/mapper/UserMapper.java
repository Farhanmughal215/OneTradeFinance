package com.xstocks.uc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OperatorVO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dongjunfu
 * @description 针对表【user(user)】的数据库操作Mapper
 * @createDate 2023-10-28 14:38:31
 * @Entity com.xstocks.uc.pojo.po.UserPO
 */
@Repository
public interface UserMapper extends BaseMapper<UserPO> {

    IPage<UserPO> selectPageUserOfOrg(Page<UserPO> page, @Param("path") String path);

    List<OperatorVO> selectOperatorOfOrg(@Param("path") String path, @Param("roleCode") String roleCode);
}




