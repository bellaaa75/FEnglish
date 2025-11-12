package org.example.fenglish.repository;

import java.time.LocalDateTime;

/**
 * 投影接口：接住 native query 里 SELECT 出来的列
 * 方法名必须和列别名完全一致！
 */
public interface CollectWordProjection {

    String getCollectId();      // c.collectId
    String getTargetId();     // c.targetId
    String getWordName();     // w.wordName
    String getWordExplain();  // w.wordExplain
    LocalDateTime getCollectTime(); // c.collectTime
}
