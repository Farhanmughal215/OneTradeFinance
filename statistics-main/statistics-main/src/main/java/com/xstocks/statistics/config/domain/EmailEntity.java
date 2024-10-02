package com.xstocks.statistics.config.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cgf
 * @description: TODO
 * @date 2021/8/6 11:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEntity implements Serializable {

    private static final long serialVersionUID = -4792078705620939949L;
    //收件人数组
    private String tos;

    private String ccEmail;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;
}
