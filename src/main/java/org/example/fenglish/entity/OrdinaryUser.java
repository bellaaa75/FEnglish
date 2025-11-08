package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "ordinaryuser")
@PrimaryKeyJoinColumn(name = "userId") // 与父表User的userID关联
public class OrdinaryUser extends User {

    @Column(name = "PhoneNumber", length = 20) // 限制手机号长度
    private String phoneNumber;

    @Column(name = "userMailbox", length = 100) // 限制邮箱长度
    private String userMailbox;

    @Column(name = "userName", length = 50, nullable = false) // 用户名非空
    private String userName;

    @Column(name = "Gender", length = 2) // 性别通常为"男"/"女"
    private String gender;

    @Column(name = "RegisterTime")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime registerTime;


    public OrdinaryUser() { }

    public OrdinaryUser(String userId, String userPassword, String phoneNumber, String userMailbox,
                        String userName, String gender, LocalDateTime registerTime) {
        super(userId, userPassword, "ORDINARY");
        this.phoneNumber = phoneNumber;
        this.userMailbox = userMailbox;
        this.userName = userName;
        this.gender = gender;
        this.registerTime = LocalDateTime.now();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserMailbox() {
        return userMailbox;
    }

    public void setUserMailbox(String userMailbox) {
        this.userMailbox = userMailbox;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }
}