package org.example.fenglish.controller;

import org.example.fenglish.entity.LearningState;
import org.example.fenglish.service.LearningStateService;
import org.example.fenglish.vo.dto.LearningStateDTO;
import org.example.fenglish.vo.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-states")
public class LearningStateController {

    @Autowired
    private LearningStateService learningStateService;

    // 增加学习状态（默认未学）
    @PostMapping
    public Result<Boolean> addLearningState(@RequestParam String userId, @RequestParam String wordId) {
        boolean success = learningStateService.addLearningState(userId, wordId);
        return Result.success(success);
    }

    // 修改学习状态
    @PutMapping
    public Result<Boolean> updateLearningState(
            @RequestParam String userId,
            @RequestParam String wordId,
            @RequestParam LearningState.LearnStateEnum state) {
        boolean success = learningStateService.updateLearningState(userId, wordId, state);
        return Result.success(success);
    }

    // 查询单个单词的学习状态
    @GetMapping
    public Result<LearningStateDTO> getLearningState(
            @RequestParam String userId,
            @RequestParam String wordId) {
        LearningStateDTO dto = learningStateService.getLearningState(userId, wordId);
        return Result.success(dto);
    }

    // 查询用户所有单词的学习状态
    @GetMapping("/user/{userId}")
    public Result<List<LearningStateDTO>> getUserAllLearningStates(@PathVariable String userId) {
        List<LearningStateDTO> dtos = learningStateService.getUserAllLearningStates(userId);
        return Result.success(dtos);
    }

    // 查询用户特定状态的单词列表
    @GetMapping("/user/{userId}/state/{state}")
    public Result<List<LearningStateDTO>> getUserLearningStatesByState(
            @PathVariable String userId,
            @PathVariable LearningState.LearnStateEnum state) {
        List<LearningStateDTO> dtos = learningStateService.getUserLearningStatesByState(userId, state);
        return Result.success(dtos);
    }

}