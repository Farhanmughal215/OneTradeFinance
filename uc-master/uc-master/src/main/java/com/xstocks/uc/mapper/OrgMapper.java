package com.xstocks.uc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.vo.OrgNodeVO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author dongjunfu
 * @description 针对表【org(org)】的数据库操作Mapper
 * @createDate 2023-10-28 14:13:13
 * @Entity com.xstocks.uc.pojo.po.OrgPO
 */
@Repository
public interface OrgMapper extends BaseMapper<OrgPO> {

    OrgNodeVO getOrgNode(Map<String, Object> map);

    OrgNodeVO getParentOrg(Map<String, Object> map);
}




