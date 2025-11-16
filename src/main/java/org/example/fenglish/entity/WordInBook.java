package org.example.fenglish.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "WordInBook")
@IdClass(WordInBook.WordInBookId.class)
public class WordInBook {

    @Id
    @Column(name = "WordID", length = 50)
    private String wordId;

    @Id
    @Column(name = "BookID", length = 50)
    private String bookId;

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public VocabularyBook getVocabularyBook() {
        return vocabularyBook;
    }

    public void setVocabularyBook(VocabularyBook vocabularyBook) {
        this.vocabularyBook = vocabularyBook;
    }

    public EnglishWords getEnglishWords() {
        return englishWords;
    }

    public void setEnglishWords(EnglishWords englishWords) {
        this.englishWords = englishWords;
    }

    @Data
    public static class WordInBookId implements Serializable {
        private String wordId;
        private String bookId;
    }

    // 关联单词表（单向或双向均可，保留）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WordID", insertable = false, updatable = false)
    private EnglishWords englishWords;

    // 关键修改：添加@JsonIgnore，忽略反向关联的单词书，避免序列化循环
    @JsonIgnore // 核心注解：序列化时不包含该字段
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID", insertable = false, updatable = false)
    private VocabularyBook vocabularyBook;
}