package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "StudyRecord")
public class StudyRecord {

    @Id
    @Column(name = "StudyRecordID")
    private String studyRecordId;

    @Column(name = "UserID")
    private String userId;

    @Column(name = "EnglishwordID")  // 注意字段名
    private String englishwordId;

    @Column(name = "StudyTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date studyTime;
}