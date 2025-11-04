package org.example.fenglish.service;

import org.example.fenglish.entity.LearningState;
import org.example.fenglish.vo.dto.LearningStateDTO;

import java.util.List;

public interface LearningStateService {
    // 增加学习状态（默认未学）
    boolean addLearningState(String userId, String wordId);

    // 修改学习状态
    boolean updateLearningState(String userId, String wordId, LearningState.LearnStateEnum state);

    // 查询用户某个单词的学习状态（返回DTO）
    LearningStateDTO getLearningState(String userId, String wordId);

    // 查询用户所有单词的学习状态（返回DTO列表）
    List<LearningStateDTO> getUserAllLearningStates(String userId);

    // 查询用户特定状态的单词列表（返回DTO列表）
    List<LearningStateDTO> getUserLearningStatesByState(String userId, LearningState.LearnStateEnum state);

}