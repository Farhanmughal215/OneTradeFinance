package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Kyc图片表
 * </p>
 *
 * @author kevin
 * @since 2024-01-12
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_kyc_image")
public class KycImage implements Serializable {


    private static final long serialVersionUID = 3499518001424066545L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Kyc ID
     */
    private Long kycId;

    /**
     * 图片地址
     */
    private String imageUrl;
}
