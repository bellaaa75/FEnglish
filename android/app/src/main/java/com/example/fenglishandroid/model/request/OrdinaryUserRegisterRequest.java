package com.example.fenglishandroid.model.request;

public class OrdinaryUserRegisterRequest {
    private String userName;
    private String userPassword;
    private String phoneNumber;
    private String userMailbox;
    private String gender;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
}
