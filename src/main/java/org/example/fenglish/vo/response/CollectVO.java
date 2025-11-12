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

        /*=== 公共字段 ===*/
        private Long collectId;     // 收藏主键
        private String targetId;    // 单词ID 或 单词书ID
        private Short targetType;   // 1 单词  2 单词书
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
        private Date collectTime;

        /*=== 仅当 targetType=1 时填充 ===*/
        private String wordName;
        private String wordExplain;

        /*=== 仅当 targetType=2 时填充 ===*/
        private String bookName;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
        private Date publishTime;
    }
}