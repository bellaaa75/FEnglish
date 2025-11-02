package org.example.fenglish.vo.request;  // 统一放在vo.request包下

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Date;

@Data
public class VocabularyBookAddReq {
    private String bookId;  // 可选（前端可传或后端生成）

    @NotBlank(message = "单词书名称不能为空")  // 参数校验：名称必传
    private String bookName;

    private Date publishTime;  // 可选（默认当前时间）
}