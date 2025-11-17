package org.example.fenglish.service.impl;

import org.example.fenglish.entity.LearningState;
import org.example.fenglish.entity.User;
import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.repository.LearningStateRepository;
import org.example.fenglish.repository.UserRepository;
import org.example.fenglish.repository.EnglishWordsRepository;
import org.example.fenglish.service.LearningStateService;
import org.example.fenglish.vo.dto.LearningStateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LearningStateServiceImpl implements LearningStateService {

    @Autowired
    private LearningStateRepository learningStateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnglishWordsRepository englishWordsRepository;
    @Autowired
    private org.example.fenglish.service.StudyRecordService studyRecordService;

    @Override
    public boolean addLearningState(String userId, String wordId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            return false;
        }

        // 验证单词是否存在
        if (!englishWordsRepository.existsById(wordId)) {
            return false;
        }

        // 检查是否已存在记录
        LearningState.LearningStateId id = new LearningState.LearningStateId();
        id.setUserId(userId);
        id.setWordId(wordId);

        if (learningStateRepository.existsById(id)) {
            return false; // 已存在记录
        }

        // 创建新记录，默认为"未学"状态
        LearningState state = new LearningState(userId, wordId, LearningState.LearnStateEnum.未学);
        learningStateRepository.save(state);
        return true;
    }

    @Override
    public boolean updateLearningState(String userId, String wordId, LearningState.LearnStateEnum state) {
        LearningState.LearningStateId id = new LearningState.LearningStateId();
        id.setUserId(userId);
        id.setWordId(wordId);

        Optional<LearningState> optionalState = learningStateRepository.findById(id);
        if (optionalState.isPresent()) {
            LearningState learningState = optionalState.get();
            learningState.setLearnState(state);
            learningStateRepository.save(learningState);

            // 如果状态被更新为 已学，则新增一条学习记录（StudyRecord）
            try {
                if (state == LearningState.LearnStateEnum.已学) {
                    System.out.println("[LearningStateService] State is 已学, creating StudyRecord for userId=" + userId + ", wordId=" + wordId);
                    // 构造 StudyRecord 并调用 StudyRecordService 保存
                    org.example.fenglish.entity.StudyRecord sr = new org.example.fenglish.entity.StudyRecord();
                    org.example.fenglish.entity.User user = userRepository.findByUserId(userId);
                    org.example.fenglish.entity.EnglishWords word = englishWordsRepository.findById(wordId).orElse(null);
                    sr.setUser(user);
                    sr.setEnglishWord(word);
                    sr.setStudyTime(new java.util.Date());
                    boolean added = studyRecordService.addStudyRecord(sr);
                    System.out.println("[LearningStateService] StudyRecord add result: " + added);
                }
            } catch (Exception e) {
                System.out.println("[LearningStateService] Failed to add StudyRecord: " + e.getMessage());
            }

            return true;
        }
        return false;
    }

    @Override
    public LearningStateDTO getLearningState(String userId, String wordId) {
        LearningState.LearningStateId id = new LearningState.LearningStateId();
        id.setUserId(userId);
        id.setWordId(wordId);

        return learningStateRepository.findById(id)
                .map(LearningStateDTO::fromEntity)  // 转换为DTO
                .orElse(null);
    }

    @Override
    public List<LearningStateDTO> getUserAllLearningStates(String userId) {
        // 注意：这里需要在Repository中定义findByUserId方法
        return learningStateRepository.findByUserId(userId).stream()
                .map(LearningStateDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LearningStateDTO> getUserLearningStatesByState(String userId, LearningState.LearnStateEnum state) {
        // 注意：这里需要在Repository中定义findByUserIdAndLearnState方法
        return learningStateRepository.findByUserIdAndLearnState(userId, state).stream()
                .map(LearningStateDTO::fromEntity)
                .collect(Collectors.toList());
    }
}