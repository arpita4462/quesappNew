package com.atrio.quesapp.model;

/**
 * Created by Arpita Patel on 29-06-2017.
 */

public class UserDetail {
    String userName;
    String createdDated;
    String emailId;
    String userId;
    String deviceId;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public UserDetail() {
    }

    public UserDetail(String userName, String createdDated, String emailId, String userId, String deviceId) {
        this.userName = userName;
        this.createdDated = createdDated;
        this.emailId = emailId;
        this.userId = userId;
        this.deviceId=deviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedDated() {
        return createdDated;
    }

    public void setCreatedDated(String createdDated) {
        this.createdDated = createdDated;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
