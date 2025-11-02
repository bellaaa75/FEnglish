package org.example.fenglish.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "LearningState")
@IdClass(LearningState.LearningStateId.class)
public class LearningState {

    // 复合主键中的 userID（与 User 表的 userID 关联）
    @Id
    @Column(name = "UserID", length = 50)
    private String userId;

    // 复合主键中的 wordID（与 EnglishWords 表的 WordID 关联）
    @Id
    @Column(name = "WordID", length = 50)
    private String wordId;

    // 关联 User 实体（可选，用于查询时级联获取用户信息）
    @ManyToOne
    @JoinColumn(name = "UserID", insertable = false, updatable = false)
    private User user;

    // 关联 EnglishWords 实体（可选，用于查询时级联获取单词信息）
    @ManyToOne
    @JoinColumn(name = "WordID", insertable = false, updatable = false)
    private EnglishWords englishWord;

    // 学习状态字段
    @Enumerated(EnumType.STRING)
    @Column(name = "LearnState", nullable = false)
    private LearnStateEnum learnState;

    // 构造方法、getter、setter（手动实现，避免 Lombok 命名问题）
    public LearningState() {}

    public LearningState(String userId, String wordId, LearnStateEnum learnState) {
        this.userId = userId;
        this.wordId = wordId;
        this.learnState = learnState;
    }

    // getter 和 setter 必须与复合主键类的属性名一致
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public EnglishWords getEnglishWord() { return englishWord; }
    public void setEnglishWord(EnglishWords englishWord) { this.englishWord = englishWord; }

    public LearnStateEnum getLearnState() { return learnState; }
    public void setLearnState(LearnStateEnum learnState) { this.learnState = learnState; }

    // 复合主键类（属性名必须与实体类的 userId、wordId 完全一致）
    public static class LearningStateId implements Serializable {
        private String userId; // 与实体类的 userId 对应
        private String wordId; // 与实体类的 wordId 对应

        // 必须重写 equals 和 hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LearningStateId that = (LearningStateId) o;
            return Objects.equals(userId, that.userId) && Objects.equals(wordId, that.wordId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, wordId);
        }

        // 必须提供 getter 和 setter（否则 JPA 无法访问属性）
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getWordId() { return wordId; }
        public void setWordId(String wordId) { this.wordId = wordId; }
    }

    // 学习状态枚举
    public enum LearnStateEnum {
        未学, 已学, 熟练掌握
    }
}