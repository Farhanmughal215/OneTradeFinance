package com.xstocks.uc.utils;

import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.OrgNodeVO;
import com.xstocks.uc.pojo.vo.UserVO;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, mappingControl = DeepClone.class)
public interface MapperUtil {
    MapperUtil INSTANCE = Mappers.getMapper(MapperUtil.class);

    UserVO userPO2VO(UserPO userPO);

    OrgNodeVO orgPO2VO(OrgPO orgPO);

    OrgNodeVO cloneOrgNodeVO(OrgNodeVO in);
}
