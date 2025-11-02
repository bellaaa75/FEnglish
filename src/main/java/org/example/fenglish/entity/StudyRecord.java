package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "StudyRecord")
public class StudyRecord {

    @Id
    @Column(name = "StudyRecordID", length = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private String studyRecordId;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "EnglishwordID", referencedColumnName = "WordID") // 关联单词表主键
    private EnglishWords englishWord;

    @Column(name = "StudyTime", nullable = false) // 学习时间非空
    @Temporal(TemporalType.TIMESTAMP)
    private Date studyTime;
}