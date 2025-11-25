// User.java
package com.example.fenglishandroid.model.request;

public class User {
    private String userId;
    private String userName;

    private String userType;
    private String gender;
    private String phoneNumber;
    private String userMailbox;
    private String registerTime;

    public User() {}

    public User(String userId, String userName, String userType, String gender, String phoneNumber, String userMailbox, String registerTime) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.userMailbox = userMailbox;
        this.registerTime = registerTime;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserType() { return userType; }

    public void setUserType(String userType) { this.userType = userType; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getUserMailbox() { return userMailbox; }
    public void setUserMailbox(String userMailbox) { this.userMailbox = userMailbox; }
    public String getRegisterTime() { return registerTime; }
    public void setRegisterTime(String registerTime) { this.registerTime = registerTime; }
}
