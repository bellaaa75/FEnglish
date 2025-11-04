// LearningStateDTO.java
package org.example.fenglish.vo.dto;

import org.example.fenglish.entity.LearningState;
import lombok.Data;

@Data
public class LearningStateDTO {
    private String userId;         // 用户ID
    private String wordId;         // 单词ID
    private String wordName;       // 单词名称
    private String wordExplain;    // 单词释义
    private LearningState.LearnStateEnum learnState;  // 学习状态

    // 从实体类转换为DTO（过滤敏感字段）
    public static LearningStateDTO fromEntity(LearningState state) {
        LearningStateDTO dto = new LearningStateDTO();
        dto.setUserId(state.getUserId());
        dto.setWordId(state.getWordId());
        dto.setLearnState(state.getLearnState());

        // 只提取单词的必要信息（避免返回完整实体）
        if (state.getEnglishWord() != null) {
            dto.setWordName(state.getEnglishWord().getWordName());
            dto.setWordExplain(state.getEnglishWord().getWordExplain());
        }

        // 不包含User信息，避免泄露密码
        return dto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LearningState.LearnStateEnum getLearnState() {
        return learnState;
    }

    public void setLearnState(LearningState.LearnStateEnum learnState) {
        this.learnState = learnState;
    }

    public String getWordExplain() {
        return wordExplain;
    }

    public void setWordExplain(String wordExplain) {
        this.wordExplain = wordExplain;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }
}