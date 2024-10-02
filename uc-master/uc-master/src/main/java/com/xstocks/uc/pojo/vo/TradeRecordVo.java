package com.xstocks.uc.pojo.vo;

import com.xstocks.uc.pojo.TradeRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Kevin
 * @date 2024/3/21 15:02
 * @apiNote
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeRecordVo
{

    private String date;

    private List<TradeRecord> recordList;
}
