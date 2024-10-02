package com.xstocks.referral.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName OrgTreeVO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/21 17:28
 **/

@Data
public class OrgNodeVO {
    private Long id;

    /**
     * id
     */
    private String code;

    /**
     * org name
     */
    private String name;

    /**
     * org level
     */
    private Integer level;

    /**
     * org code path
     */
    private String orgCodePath;

    /**
     * org name path
     */
    private String orgNamePath;

    private String parentOrgCode;

    /**
     * parent org
     */
    private OrgNodeVO parent;

    private List<OrgNodeVO> children;


}
