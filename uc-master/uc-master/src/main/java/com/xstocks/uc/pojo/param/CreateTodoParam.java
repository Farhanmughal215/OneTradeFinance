package com.xstocks.uc.pojo.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateTodoParam {

    @NotNull(message = "todoType required")
    /**
     * todo type,0用户资料审核1银行卡审核2充值3提现
     */
    private Integer todoType;


    /**
     * attachments
     */
    private List<MultipartFile> attachments;

    /**
     * description
     */
    private String description;

    private Long userId;
}
