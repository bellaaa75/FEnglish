package org.example.fenglish.repository;

import java.time.LocalDateTime;

public interface CollectBookProjection {

    String getCollectId();
    String getTargetId();
    String getBookName();      // b.bookName
    LocalDateTime getPublishTime(); // b.publishTime
    LocalDateTime getCollectTime();
}
