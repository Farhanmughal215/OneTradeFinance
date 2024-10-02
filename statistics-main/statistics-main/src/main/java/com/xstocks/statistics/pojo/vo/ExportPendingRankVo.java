package com.xstocks.statistics.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@HeadFontStyle(bold = false, fontHeightInPoints = 11)
@HeadStyle(fillForegroundColor = 9, leftBorderColor = 70, rightBorderColor = 70, bottomBorderColor = 70, wrapped = false, shrinkToFit = true)
public class ExportPendingRankVo extends BaseRowModel {

    @ExcelProperty(value = "排名", index = 0)
    private String rankNo;

    @ExcelProperty(value = "用户ID", index = 1)
    private Long uid;

    @ExcelProperty(value = "钱包地址", index = 2)
    private String address;

    @ExcelProperty(value = "数值", index = 3)
    private String amount;

}
