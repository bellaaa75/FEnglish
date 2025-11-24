package com.example.fenglishandroid.model;

import java.util.Date;

public class CollectBookDTO {
    private String collectId;
    private String targetId;
    private String bookName;
    private Date publishTime;   // ✅ Date
    private Date collectTime;   // ✅ Date
    private boolean showDateHeader = true;

    public String getCollectId() { return collectId; }
    public void setCollectId(String collectId) { this.collectId = collectId; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
    public Date getCollectTime() { return collectTime; }
    public void setCollectTime(Date collectTime) { this.collectTime = collectTime; }
    public boolean isShowDateHeader() {
        return showDateHeader;
    }

    public void setShowDateHeader(boolean showDateHeader) {
        this.showDateHeader = showDateHeader;
    }
}