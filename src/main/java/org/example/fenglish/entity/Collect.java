package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "Collect",
        uniqueConstraints = @UniqueConstraint(columnNames = {"WordID", "userID"})) // 联合唯一（同一用户不能重复收藏同一单词）
public class Collect {

    @Id
    @Column(name = "CollectID", length = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private String collectId;

    @ManyToOne // 多对一：多个收藏对应一个单词
    @JoinColumn(name = "WordID", referencedColumnName = "WordID")
    private EnglishWords englishWord;

    @ManyToOne // 多对一：多个收藏对应一个用户
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    private User user;

    @Column(name = "CollectTime", nullable = false) // 收藏时间非空
    @Temporal(TemporalType.TIMESTAMP)
    private Date collectTime;
}