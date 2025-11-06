package org.example.fenglish.vo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class WordDTO {
    private String wordId;      // 单词主键
    private String wordName;    // 英文单词
    private String meaning;     // 中文释义
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date collectTime;   // 收藏时间
}