package org.example.fenglish.service;

import org.example.fenglish.entity.StudyRecord;
import org.example.fenglish.vo.response.StudyStatisticVO;

import java.util.Date;
import java.util.List;

public interface StudyRecordService {
    // 增加学习记录
    boolean addStudyRecord(StudyRecord studyRecord);

    // 设置学习记录ID
    boolean setStudyRecordID(StudyRecord studyRecord, String studyRecordId);

    // 返回学习记录ID
    String getStudyRecordID(StudyRecord studyRecord);

    // 返回学习时间
    String getStudyTime(StudyRecord studyRecord);

    // 设置学习时间
    boolean setStudyTime(StudyRecord studyRecord, Date studyTime);

    // 返回用户ID
    String getUserID(StudyRecord studyRecord);

    // 设置用户ID
    boolean setUserID(StudyRecord studyRecord, String userId);

    // 设置单词ID
    boolean setEnglishWordID(StudyRecord studyRecord, String wordId);

    // 返回单词ID
    String getEnglishWordID(StudyRecord studyRecord);

    StudyStatisticVO getMonthlyStudyStats(String userId);
}