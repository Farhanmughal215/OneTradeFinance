package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xstocks.uc.pojo.dto.marketaux.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 资讯表
 * </p>
 *
 * @author kevin
 * @since 2024-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_information")
public class Information implements Serializable {


    private static final long serialVersionUID = 712112186712351418L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型,1:新闻,2:economy,3:7x24
     */
    private Integer type;

    private String lang;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 来源
     */
    private String source;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String description;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 发布时间
     */
    private Date publishedAt;

    /**
     * 创建时间
     */
    private Date createTime;

    public Information(PageDTO.NewsItem item, Integer type, String lang) {
        this.uuid = item.getUuid();
        this.source = item.getSource();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imageUrl = item.getImage_url();
        this.publishedAt = item.getPublished_at();
        this.type = type;
        this.lang = lang;
        this.createTime = new Date();
    }
}
