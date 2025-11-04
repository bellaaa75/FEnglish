package org.example.fenglish.controller;

import org.example.fenglish.entity.StudyRecord;
import org.example.fenglish.repository.StudyRecordRepository;
import org.example.fenglish.service.StudyRecordService;
import org.example.fenglish.vo.response.Result;
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

}