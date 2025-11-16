package org.example.fenglish.vo.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class VocabularyBookDetailResp {
    private String bookId;
    private String bookName;
    private LocalDateTime publishTime;
    private List<WordSimpleResp> wordList;  // 关联的单词列表

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public List<WordSimpleResp> getWordList() {
        return wordList;
    }

    public void setWordList(List<WordSimpleResp> wordList) {
        this.wordList = wordList;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}