package com.xstocks.statistics.pojo.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportRankParam {

    /**
     * 导入已入榜的榜单类型：
     * day - 日榜
     * overall - 总榜
     */
    private String rankType;

    /**
     * 导入已入榜的榜单子类型：
     *  trade_count:交易笔数；
     *  realize:实现盈亏；
     *  profit_rate:收益率；
     */
    private String rankSubType;

    /**
     * 指定日期的已放榜，格式：yyyy-MM-dd
     */
    private String date;

    /**
     * 将对应待放榜的第多少名开始的数据同步到已放榜中
     *    注：已放榜的数据来源：是从待放榜中导出前200名(导出时可指定top值)，运营会对这200名进行删减改动，然后把数据给到我们
     *       所以最终已放榜的数据，是包括运营返回的数据，加上待放榜中第201名开始的数据，两者组合在一起，就是一个已放榜的全量数据，只是在给前端展示已放榜时固定只展示前100名
     */
    private Integer startRankNo = 201;

    /**
     * excel文件
     */
    private MultipartFile file;

}
