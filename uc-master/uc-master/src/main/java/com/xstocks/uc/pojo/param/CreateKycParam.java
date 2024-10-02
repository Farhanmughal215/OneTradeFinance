package com.xstocks.uc.pojo.param;

import lombok.Data;

import java.util.List;

@Data
public class CreateKycParam {

    private Long userId;

    private String userName;

    private String address;

    private List<String> cardImages;
}
