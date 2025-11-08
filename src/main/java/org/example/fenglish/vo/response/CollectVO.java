package org.example.fenglish.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class CollectVO {

    /** 收藏总条数 */
    private long total;

    /** 当前页数据 */
    private List<CollectItem> items;

    @Data
    public static class CollectItem {
        /** 单词ID */
        private String wordId;
        /** 单词名称 */
        private String wordName;
        /** 单词释义 */
        private String wordExplain;
        /** 收藏时间 */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
        private Date collectTime;
    }
}