package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "OrdinaryUser")
@PrimaryKeyJoinColumn(name = "userID") // 与父表User的userID关联
public class OrdinaryUser extends User {

    @Column(name = "PhoneNumber", length = 20) // 限制手机号长度
    private String phoneNumber;

    @Column(name = "userMailbox", length = 100) // 限制邮箱长度
    private String userMailbox;

    @Column(name = "userName", length = 50, nullable = false) // 用户名非空
    private String userName;

    @Column(name = "Gender", length = 2) // 性别通常为"男"/"女"
    private String gender;

    @Column(name = "RegisterTime", nullable = false) // 注册时间非空
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;
}