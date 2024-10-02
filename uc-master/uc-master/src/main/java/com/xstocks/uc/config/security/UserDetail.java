package com.xstocks.uc.config.security;


import com.xstocks.uc.pojo.po.UserPO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 自定义用户对象
 *
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserDetail extends User {
    /**
     * 我们自己的用户实体对象，这里省略掉get/set方法
     */
    private UserPO userPOEntity;

    public UserDetail(UserPO userPOEntity, Collection<? extends GrantedAuthority> authorities) {
        // 必须调用父类的构造方法，初始化用户名、密码、权限
        super(userPOEntity.getPhone(), userPOEntity.getPsw(), authorities);
        this.userPOEntity = userPOEntity;
    }
}
