package com.xstocks.uc.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Kevin
 * @date 2024/1/12 11:16
 * @apiNote UserKycVo
 */
@Data
public class UserKycVo {

    private String userName;

    private String address;

    private List<String> cardImages;
}
