package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model;

public class UserInfo {

    private String userId;
    private String userToken;
    private String userFullName;
    private String userImageURL;

    public UserInfo() {}

    public UserInfo(String userId, String userToken, String userFullName, String userImageURL) {
        this.userId = userId;
        this.userToken = userToken;
        this.userFullName = userFullName;
        this.userImageURL = userImageURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }
}
