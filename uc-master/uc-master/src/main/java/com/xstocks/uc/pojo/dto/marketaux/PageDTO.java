package com.xstocks.uc.pojo.dto.marketaux;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class PageDTO {
    private Meta meta;

    private List<NewsItem> data;

    @Data
    public static class Meta {
        private Long found;
        private Long returned;
        private Long limit;
        private Long page;
    }

    @Data
    public static class NewsItem {
        private String uuid;
        private String source;
        private String title;
        private String description;
        private String image_url;

        @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss.SSSSSSZ")
        private Date published_at;
    }
}


