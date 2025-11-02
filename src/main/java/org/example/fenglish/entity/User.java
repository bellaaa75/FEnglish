package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "User")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @Column(name = "userID", length = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键策略
    private String userId;

    @Column(name = "userPassword", nullable = false)
    private String userPassword;
}