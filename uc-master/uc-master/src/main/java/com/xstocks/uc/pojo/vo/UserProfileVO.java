package com.xstocks.uc.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileVO {
    private String userName;
    private String address;
    private String bankNo;
}
