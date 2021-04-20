package com.ehealthcaremanagement.models.custom;

public class PasswordChangeModel {

    private String password;
    private String newPassword;
    private String retype;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetype() {
        return retype;
    }

    public void setRetype(String retype) {
        this.retype = retype;
    }
}
