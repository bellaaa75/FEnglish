package com.example.fenglishandroid.model.request;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String verifyCode;
    private boolean usePasswordVerification;

    public ChangePasswordRequest() {}

    // Getters and Setters
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getVerifyCode() { return verifyCode; }
    public void setVerifyCode(String verifyCode) { this.verifyCode = verifyCode; }
    public boolean isUsePasswordVerification() { return usePasswordVerification; }
    public void setUsePasswordVerification(boolean usePasswordVerification) { this.usePasswordVerification = usePasswordVerification; }
}
