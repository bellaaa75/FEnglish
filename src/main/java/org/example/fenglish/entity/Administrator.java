package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "Administrator")
@PrimaryKeyJoinColumn(name = "userID") // 与父表User的userID关联
public class Administrator extends User {
    // 继承父类属性，无额外字段
}