package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "VocabularyBook")
public class VocabularyBook {

    @Id
    @Column(name = "BookID")
    private String bookId;

    @Column(name = "BookName", nullable = false)
    private String bookName;

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "PublishTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishTime;
}