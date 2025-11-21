package com.example.fenglishandroid.model.request;

public class UpdateUserRequest {
    private String userName;
    private String gender;
    private String phoneNumber;
    private String userMailbox;

    public UpdateUserRequest() {}

    // Getters and Setters
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getUserMailbox() { return userMailbox; }
    public void setUserMailbox(String userMailbox) { this.userMailbox = userMailbox; }
}
