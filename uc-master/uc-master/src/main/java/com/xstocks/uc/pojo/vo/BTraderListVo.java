package com.xstocks.uc.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Kevin
 * @date 2024/3/25 14:34
 * @apiNote
 */
@Data
public class BTraderListVo {
    /**
     *
     t.id ,
     u.tx_address AS address,
     u.nick_name AS nickName,
     u.synopsis ,
     t.`status`,
     t.create_time AS applyTime,
     t.contract_certificate AS contractCertificate
     */

    private Integer id;

    private String address;

    private String nickName;

    private String synopsis;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String contractCertificate;
}
