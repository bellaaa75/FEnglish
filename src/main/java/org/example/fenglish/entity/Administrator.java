package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "Administrator")
@PrimaryKeyJoinColumn(name = "userID")
public class Administrator extends User {
    // 管理员只有基础用户属性
}