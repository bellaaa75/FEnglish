package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "VocabularyBook")
public class VocabularyBook {

    @Id
    @Column(name = "BookID", length = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private String bookId;

    @Column(name = "BookName", nullable = false, length = 100) // 词书名非空
    private String bookName;

    @Column(name = "Content", columnDefinition = "TEXT") // 词书描述
    private String content;

    @Column(name = "PublishTime", nullable = false) // 发布时间非空
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishTime;
}