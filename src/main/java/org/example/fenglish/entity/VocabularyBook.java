package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "VocabularyBook")
public class VocabularyBook {

    @Id
    @Column(name = "BookID", length = 50)
    private String bookId;

    @Column(name = "BookName", nullable = false, length = 100) // 词书名非空
    private String bookName;

    @Column(name = "Content", columnDefinition = "TEXT") // 词书描述
    private String content;

    @Column(name = "PublishTime", columnDefinition = "TIMESTAMP", nullable = false) // 发布时间非空
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime publishTime;

    // 【关联关系】一个单词书包含多个单词（通过word_in_book关联）
    @OneToMany(mappedBy = "vocabularyBook", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WordInBook> wordInBooks;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public List<WordInBook> getWordInBooks() {
        return wordInBooks;
    }

    public void setWordInBooks(List<WordInBook> wordInBooks) {
        this.wordInBooks = wordInBooks;
    }
}