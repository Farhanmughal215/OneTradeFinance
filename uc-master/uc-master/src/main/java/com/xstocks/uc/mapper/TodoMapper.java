package com.xstocks.uc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstocks.uc.pojo.po.TodoPO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

/**
 * @author firtuss
 * @description 针对表【todo】的数据库操作Mapper
 * @createDate 2023-10-28 22:12:22
 * @Entity com.xstocks.uc.pojo.po.TodoPO
 */
@Repository
public interface TodoMapper extends BaseMapper<TodoPO> {
    IPage<TodoPO> getMyToDo(Page<TodoPO> page,
                            @Param("path") String path,
                            @Param("userId") Long userId,
                            @Param("userBizId") String userBizId,
                            @Param("phone") String phone,
                            @Param("todoType") Integer todoType,
                            @Param("todoStatus") Integer todoStatus);
}




