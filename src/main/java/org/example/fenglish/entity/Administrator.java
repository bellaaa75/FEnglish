package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "Administrator")
@PrimaryKeyJoinColumn(name = "userID")  // 子表通过userID与父表关联
public class Administrator extends User {
    // 管理员只有基础用户属性
}