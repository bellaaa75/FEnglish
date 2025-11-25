package com.example.fenglishandroid.model.request;

public class DeleteAccountRequest {
    private String password;


    public DeleteAccountRequest() {
    }

    public DeleteAccountRequest(String password) {
        this.password = password;
    }

    // Getters and Setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
