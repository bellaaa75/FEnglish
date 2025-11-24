package com.example.fenglishandroid.model;

import java. util.Date;

public class CollectWordDTO {
    private String collectId;
    private String targetId;
    private String wordName;
    private String wordExplain;
    private Date collectTime;
    private boolean showDateHeader = true;

    public String getCollectId() { return collectId; }
    public void setCollectId(String collectId) { this.collectId = collectId; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getWordName() { return wordName; }
    public void setWordName(String wordName) { this.wordName = wordName; }
    public String getWordExplain() { return wordExplain; }
    public void setWordExplain(String wordExplain) { this.wordExplain = wordExplain; }
    public Date getCollectTime() { return collectTime; }
    public void setCollectTime(Date collectTime) { this.collectTime = collectTime; }
    public boolean isShowDateHeader() {
        return showDateHeader;
    }

    public void setShowDateHeader(boolean showDateHeader) {
        this.showDateHeader = showDateHeader;
    }
}