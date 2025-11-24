package com.example.fenglishandroid.model;

public class VocabularyBookSimpleResp {
    private String bookId;
    private String bookName;
    private String publishTime;
    private int wordCount;
    private boolean collected;

    // getter和setter
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }
    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
}