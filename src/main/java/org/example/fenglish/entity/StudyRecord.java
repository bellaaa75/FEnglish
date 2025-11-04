package org.example.fenglish.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "StudyRecord")
public class StudyRecord {

    @Id
    @Column(name = "StudyRecordID", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String studyRecordId;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "EnglishwordID", referencedColumnName = "WordID") // 关联单词表主键
    private EnglishWords englishWord;

    @Column(name = "StudyTime", nullable = false) // 学习时间非空
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date studyTime;

    public String getStudyRecordId() {
        return studyRecordId;
    }

    public void setStudyRecordId(String studyRecordId) {
        this.studyRecordId = studyRecordId;
    }

    public Date getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(Date studyTime) {
        this.studyTime = studyTime;
    }

    // 关键：添加 user 的 getter 方法（解决报错的核心）
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // 关键：添加 englishWord 的 getter 方法
    public EnglishWords getEnglishWord() {
        return englishWord;
    }

    public void setEnglishWord(EnglishWords englishWord) {
        this.englishWord = englishWord;
    }
}