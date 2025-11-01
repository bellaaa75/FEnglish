package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity // 标记这个类是一个JPA实体，对应数据库中的一张表
@Table(name = "User") // 指定对应的数据库表名
@Inheritance(strategy = InheritanceType.JOINED)  // 继承策略：每个类一张表
public class User {

    @Id    // 标记这个字段是表的主键
    @Column(name = "userID")  // 指定对应的数据库列名
    private String userId;

    @Column(name = "userPassword", nullable = false)  // nullable=false表示不能为null
    private String userPassword;
}