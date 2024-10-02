package com.xstocks.uc.pojo.param;

import lombok.Data;

@Data
public class ApprovalKycParam {

    private Long id;

    private Long updateBy;

    private String remark;

    //状态,0:待审核,1:审核通过,-1:审核拒绝
    private Integer state;
}
