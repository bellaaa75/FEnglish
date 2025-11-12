package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "collect",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"userID", "targetID", "targetType"}))
public class Collect {

    @Id
    @Column(name = "collectID", length = 50, nullable = false)
    private String collectId;              // 不再用 Long

    @Column(name = "userID", length = 50, nullable = false)
    private String userId;

    @Column(name = "targetID", length = 50, nullable = false)
    private String targetId;

    @Column(name = "targetType", nullable = false)
    private Byte targetType;   // 1 单词  2 单词书

    @Column(name = "collectTime", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date collectTime;
}