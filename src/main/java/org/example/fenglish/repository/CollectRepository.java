package org.example.fenglish.repository;

import org.example.fenglish.entity.Collect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CollectRepository extends JpaRepository<Collect, String> {

    /* 联合唯一判断：原生字段 */
    boolean existsByEnglishWord_WordIdAndUser_UserId(String wordId, String userId);

    /* 删除 */
    int deleteByEnglishWord_WordIdAndUser_UserId(String wordId, String userId);

    /* 我的收藏：连表返回真实单词信息 */
    @Query("select c from Collect c join fetch c.englishWord where c.user.userId = :userId")
    Page<Collect> findByUser_UserId(@Param("userId") String userId, Pageable pageable);

    /* 统计用户收藏数量 */
    long countByUser_UserId(String userId);
}