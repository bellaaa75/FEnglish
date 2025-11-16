package org.example.fenglish.vo.request;  // 统一放在vo.request包下

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VocabularyBookAddReq {
    private String bookId;  // 可选（前端可传或后端生成）

    @NotBlank(message = "单词书名称不能为空")  // 参数校验：名称必传
    private String bookName;

    private LocalDateTime publishTime;  // 可选（默认当前时间）

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public @NotBlank(message = "单词书名称不能为空") String getBookName() {
        return bookName;
    }

    public void setBookName(@NotBlank(message = "单词书名称不能为空") String bookName) {
        this.bookName = bookName;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}