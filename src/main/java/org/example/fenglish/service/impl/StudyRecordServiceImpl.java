package org.example.fenglish.service.impl;

import org.example.fenglish.entity.EnglishWords;
import org.example.fenglish.entity.StudyRecord;
import org.example.fenglish.entity.User;
import org.example.fenglish.repository.EnglishWordsRepository;
import org.example.fenglish.repository.StudyRecordRepository;
import org.example.fenglish.repository.UserRepository;
import org.example.fenglish.service.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudyRecordServiceImpl implements StudyRecordService {

    private static final Logger logger = LoggerFactory.getLogger(StudyRecordServiceImpl.class);

    @Autowired
    private StudyRecordRepository studyRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnglishWordsRepository englishWordsRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean addStudyRecord(StudyRecord studyRecord) {
        // 日志：记录方法开始执行
        logger.info("===== 开始添加学习记录 =====");

        try {
            // 1. 打印传入的studyRecord对象信息
            if (studyRecord == null) {
                logger.error("传入的studyRecord为null，无法添加");
                return false;
            }
            logger.info("传入的学习记录ID：{}", studyRecord.getStudyRecordId());
            logger.info("传入的学习时间：{}", studyRecord.getStudyTime() != null ?
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(studyRecord.getStudyTime()) : "null");

            // 2. 验证关联的用户
            User user = studyRecord.getUser();
            logger.info("关联的用户对象：{}", user != null ? "非空" : "null");
            if (user == null) {
                logger.error("学习记录关联的用户为null，验证失败");
                return false;
            }

            String userId = user.getUserId();
            logger.info("关联的用户ID：{}", userId);
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("用户ID为空（null或空字符串），验证失败");
                return false;
            }

            boolean userExists = userRepository.existsById(userId);
            logger.info("用户ID[{}]是否存在于数据库：{}", userId, userExists);
            if (!userExists) {
                logger.error("用户ID[{}]在数据库中不存在，验证失败", userId);
                return false;
            }

            // 3. 验证关联的单词
            EnglishWords word = studyRecord.getEnglishWord();
            logger.info("关联的单词对象：{}", word != null ? "非空" : "null");
            if (word == null) {
                logger.error("学习记录关联的单词为null，验证失败");
                return false;
            }

            String wordId = word.getWordID(); // 注意：这里的方法名必须和实体类一致（如getWordId）
            logger.info("关联的单词ID：{}", wordId);
            if (wordId == null || wordId.trim().isEmpty()) {
                logger.error("单词ID为空（null或空字符串），验证失败");
                return false;
            }

            boolean wordExists = englishWordsRepository.existsById(wordId);
            logger.info("单词ID[{}]是否存在于数据库：{}", wordId, wordExists);
            if (!wordExists) {
                logger.error("单词ID[{}]在数据库中不存在，验证失败", wordId);
                return false;
            }

            // 4. 所有验证通过，保存记录
            StudyRecord savedRecord = studyRecordRepository.save(studyRecord);
            logger.info("学习记录保存成功！保存后的记录ID：{}", savedRecord.getStudyRecordId());
            return true;

        } catch (Exception e) {
            // 打印异常堆栈信息，方便定位错误
            logger.error("添加学习记录时发生异常：", e);
            return false;
        } finally {
            logger.info("===== 结束添加学习记录 =====");
        }
    }

    @Override
    public boolean setStudyRecordID(StudyRecord studyRecord, String studyRecordId) {
        try {
            studyRecord.setStudyRecordId(studyRecordId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getStudyRecordID(StudyRecord studyRecord) {
        return studyRecord.getStudyRecordId();
    }

    @Override
    public String getStudyTime(StudyRecord studyRecord) {
        Date studyTime = studyRecord.getStudyTime();
        return studyTime != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(studyTime) : null;
    }

    @Override
    public boolean setStudyTime(StudyRecord studyRecord, Date studyTime) {
        try {
            studyRecord.setStudyTime(studyTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserID(StudyRecord studyRecord) {
        User user = studyRecord.getUser();
        return user != null ? user.getUserId() : null;
    }

    @Override
    public boolean setUserID(StudyRecord studyRecord, String userId) {
        try {
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                return false;
            }
            studyRecord.setUser(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setEnglishWordID(StudyRecord studyRecord, String wordId) {
        try {
            EnglishWords word = englishWordsRepository.findById(wordId).orElse(null);
            if (word == null) {
                return false;
            }
            studyRecord.setEnglishWord(word);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getEnglishWordID(StudyRecord studyRecord) {
        EnglishWords word = studyRecord.getEnglishWord();
        return word != null ? word.getWordID() : null;
    }

}