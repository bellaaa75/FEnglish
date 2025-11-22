package com.example.fenglishandroid.model.request;

import java.time.LocalDateTime;

//修改单词书请求模型

public class VocabularyBookAddReq {
        // 单词书ID（可选，前端可传或后端生成）
        private String bookId;
        // 单词书名称（必填）
        private String bookName;
        // 发布时间（可选，默认当前时间）
        private String publishTime;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}