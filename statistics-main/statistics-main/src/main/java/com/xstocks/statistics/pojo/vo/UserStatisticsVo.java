package com.xstocks.statistics.pojo.vo;

import com.xstocks.statistics.pojo.po.UserStatistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsVo {

    private String type;

    private String name;

    private List<UserStatistics> dataList;

}
