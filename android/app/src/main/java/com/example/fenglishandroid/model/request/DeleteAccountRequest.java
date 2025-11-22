package com.example.fenglishandroid.model.request;

public class DeleteAccountRequest {
    private String password;
    private String verifyCode;
    private boolean usePasswordVerification;

    public DeleteAccountRequest() {}

    // Getters and Setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getVerifyCode() { return verifyCode; }
    public void setVerifyCode(String verifyCode) { this.verifyCode = verifyCode; }
    public boolean isUsePasswordVerification() { return usePasswordVerification; }
    public void setUsePasswordVerification(boolean usePasswordVerification) { this.usePasswordVerification = usePasswordVerification; }
}
