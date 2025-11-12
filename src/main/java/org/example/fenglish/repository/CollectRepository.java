package org.example.fenglish.repository;

import org.example.fenglish.entity.Collect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface CollectRepository extends JpaRepository<Collect, String> {

    /* 是否已收藏 */
    boolean existsByUserIdAndTargetIdAndTargetType(String userId,
                                                   String targetId,
                                                   Byte  targetType);

    /* 取消收藏 */
    int deleteByUserIdAndTargetIdAndTargetType(String userId,
                                               String targetId,
                                               Byte  targetType);

    /* 我的收藏（单词） */
    @Query(value = """
    SELECT c.collectId   AS collectId,
           c.targetId    AS targetId,
           w.wordName    AS wordName,
           w.wordExplain AS wordExplain,
           c.collectTime AS collectTime
    FROM Collect c                       /* 实体类名 */
    JOIN EnglishWords w ON c.targetId = w.wordId
    WHERE c.userId = :userId AND c.targetType = 1
    ORDER BY c.collectTime DESC
    """,
            countQuery = """
        SELECT COUNT(c)
        FROM Collect c
        WHERE c.userId = :userId AND c.targetType = 1
        """)
    Page<CollectWordProjection> findWordCollects(@Param("userId") String userId, Pageable pageable);

    /* 我的收藏（单词书） */
    @Query(value = """
    SELECT c.collectId    AS collectId,
           c.targetId     AS targetId,
           b.bookName     AS bookName,
           b.publishTime  AS publishTime,
           c.collectTime  AS collectTime
    FROM Collect c
    JOIN VocabularyBook b ON c.targetId = b.bookId
    WHERE c.userId = :userId AND c.targetType = 2
    ORDER BY c.collectTime DESC
    """,
            countQuery = """
        SELECT COUNT(c)
        FROM Collect c
        WHERE c.userId = :userId AND c.targetType = 2
        """)
    Page<CollectBookProjection> findBookCollects(@Param("userId") String userId, Pageable pageable);
    /* 统计用户收藏数量 */
    long countByUserId(String userId);
}