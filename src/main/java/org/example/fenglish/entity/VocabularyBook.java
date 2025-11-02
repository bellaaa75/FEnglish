package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
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

    @Column(name = "PublishTime", nullable = false) // 发布时间非空
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishTime;

    // 【关联关系】一个单词书包含多个单词（通过word_in_book关联）
    @OneToMany(mappedBy = "vocabularyBook", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WordInBook> wordInBooks;
}