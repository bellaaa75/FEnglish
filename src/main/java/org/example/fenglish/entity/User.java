package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @Column(name = "userID", nullable = false, unique = true, length = 50)

    private String userId;

    @Column(name = "userPassword", nullable = false)
    private String userPassword;

    @Column(name = "userType", nullable = false, length = 10)
    private String userType; // "ORDINARY" or "ADMIN"

    public User(){ }

    public User(String userId, String userPassword, String userType) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}