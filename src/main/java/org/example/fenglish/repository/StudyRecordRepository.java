package org.example.fenglish.repository;

import org.example.fenglish.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, String> {

    // 根据用户ID查询学习记录
    List<StudyRecord> findByUserUserId(String userId);

    // 统计用户学习的单词总数
    @Query("SELECT COUNT(DISTINCT sr.englishWord.wordId) FROM StudyRecord sr WHERE sr.user.userId = :userId")
    long countDistinctWordsByUserId(@Param("userId") String userId);

    // 1. 本月学习的单词总数
    @Query("SELECT COUNT(DISTINCT sr.englishWord.wordId) FROM StudyRecord sr " +
            "WHERE sr.user.userId = :userId " +
            "AND sr.studyTime >= :firstDayOfMonth " +
            "AND sr.studyTime < :firstDayOfNextMonth")
    long countMonthlyDistinctWords(@Param("userId") String userId,
                                   @Param("firstDayOfMonth") Date firstDayOfMonth,  // 改为 Date
                                   @Param("firstDayOfNextMonth") Date firstDayOfNextMonth);  // 改为 Date

    // 2. 本月打卡天数
    @Query("SELECT COUNT(DISTINCT FUNCTION('DATE', sr.studyTime)) FROM StudyRecord sr " +
            "WHERE sr.user.userId = :userId " +
            "AND sr.studyTime >= :firstDayOfMonth " +
            "AND sr.studyTime < :firstDayOfNextMonth")
    long countMonthlyStudyDays(@Param("userId") String userId,
                               @Param("firstDayOfMonth") Date firstDayOfMonth,  // 改为 Date
                               @Param("firstDayOfNextMonth") Date firstDayOfNextMonth);  // 改为 Date

    // 3. 本月每天学习的单词数
    @Query("SELECT FUNCTION('DATE', sr.studyTime) as studyDate, " +
            "COUNT(DISTINCT sr.englishWord.wordId) as wordCount " +
            "FROM StudyRecord sr " +
            "WHERE sr.user.userId = :userId " +
            "AND sr.studyTime >= :firstDayOfMonth " +
            "AND sr.studyTime < :firstDayOfNextMonth " +
            "GROUP BY FUNCTION('DATE', sr.studyTime) " +
            "ORDER BY FUNCTION('DATE', sr.studyTime)")
    List<Map<String, Object>> countDailyWordsInMonth(@Param("userId") String userId,
                                                     @Param("firstDayOfMonth") Date firstDayOfMonth,  // 改为 Date
                                                     @Param("firstDayOfNextMonth") Date firstDayOfNextMonth);  // 改为 Date
}
