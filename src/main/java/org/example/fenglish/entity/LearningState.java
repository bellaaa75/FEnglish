package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "LearningState")
@IdClass(LearningState.LearningStateId.class)
public class LearningState {

    @Id
    @Column(name = "UserID")
    private String userId;

    @Id
    @Column(name = "WordID")
    private String wordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "LearnState")
    private LearnStateEnum learnState;

    // 复合主键类
    @Data
    public static class LearningStateId implements Serializable {
        private String userId;
        private String wordId;
    }

    // 学习状态枚举
    public enum LearnStateEnum {
        未学, 已学, 熟练掌握
    }
}