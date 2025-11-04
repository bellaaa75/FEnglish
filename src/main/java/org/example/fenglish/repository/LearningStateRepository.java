package org.example.fenglish.repository;

import org.example.fenglish.entity.LearningState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningStateRepository extends JpaRepository<LearningState, LearningState.LearningStateId> {

    // 根据用户ID查询所有学习状态
    List<LearningState> findByUserId(String userId);

    // 根据用户ID和学习状态查询
    List<LearningState> findByUserIdAndLearnState(String userId, LearningState.LearnStateEnum state);
}