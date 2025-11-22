package com.example.fenglishandroid.model.request;


//修改单词书请求模型
public class VocabularyBookUpdateReq {
    // 单词书名称（可选：若传递则修改名称，不传递则保持原名称）
    private String bookName;
    // 发布时间（可选：若传递则修改发布时间，不传递则保持原时间）
    private String publishTime;

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