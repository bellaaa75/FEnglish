package org.example.fenglish.repository;

import org.example.fenglish.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, String> {

    // 根据用户ID查询学习记录
    List<StudyRecord> findByUserUserId(String userId);

    // 统计用户学习的单词总数
    @Query("SELECT COUNT(DISTINCT sr.englishWord.wordId) FROM StudyRecord sr WHERE sr.user.userId = :userId")
    long countDistinctWordsByUserId(@Param("userId") String userId);
}