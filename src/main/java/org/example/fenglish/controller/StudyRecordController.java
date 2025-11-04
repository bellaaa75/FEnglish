package org.example.fenglish.controller;

import org.example.fenglish.entity.StudyRecord;
import org.example.fenglish.repository.StudyRecordRepository;
import org.example.fenglish.service.StudyRecordService;
import org.example.fenglish.vo.response.Result;
import org.example.fenglish.vo.response.StudyStatisticVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {

    @Autowired
    private StudyRecordService studyRecordService;

    @Autowired
    private StudyRecordRepository studyRecordRepository;

    // 增加学习记录
    @PostMapping
    public Result<Boolean> addStudyRecord(@RequestBody StudyRecord studyRecord) {
        try {
            boolean success = studyRecordService.addStudyRecord(studyRecord);
            return Result.success(success); // 直接返回操作结果（true/false）
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    @GetMapping("/statistics")
    public Result<?> getStudyStatistics(@RequestParam String userId) { // 改为Result<?>
        try {
            StudyStatisticVO stats = studyRecordService.getMonthlyStudyStats(userId);
            return Result.success(stats); // 成功时返回Result<StudyStatisticVO>
        } catch (Exception e) {
            // 失败时返回Result<Void>，与Result<?>兼容
            return Result.fail(500, "获取统计数据失败: " + e.getMessage());
        }
    }

}