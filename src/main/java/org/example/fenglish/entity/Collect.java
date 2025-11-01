package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "Collect")
public class Collect {

    @Id
    @Column(name = "CollectID")
    private String collectId;

    @Column(name = "WordID")
    private String wordId;

    @Column(name = "userID")
    private String userId;

    @Column(name = "CollectTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date collectTime;
}