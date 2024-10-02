package com.xstocks.statistics.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RemainUserVo implements Serializable {

    private static final long serialVersionUID = -6920065921635509720L;
    private String address;


    private Boolean newAddress;

}
