package com.example.fenglishandroid.model;

import java.util.List;

public class VocabularyBookDetailResp {
    private String bookId;
    private String bookName;
    private String publishTime;
    private List<WordSimpleResp> wordList;

    // getter和setter
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }
    public List<WordSimpleResp> getWordList() { return wordList; }
    public void setWordList(List<WordSimpleResp> wordList) { this.wordList = wordList; }
}