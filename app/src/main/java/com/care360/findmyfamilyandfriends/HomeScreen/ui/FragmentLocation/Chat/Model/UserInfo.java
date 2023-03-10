package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

    private String userId;
    private String userToken;
    private String userFullName;
    private String userImageURL;

    public UserInfo(String userId, String userToken, String userFullName, String userImageURL) {
        this.userId = userId;
        this.userToken = userToken;
        this.userFullName = userFullName;
        this.userImageURL = userImageURL;
    }

    protected UserInfo(Parcel in) {
        userId = in.readString();
        userToken = in.readString();
        userFullName = in.readString();
        userImageURL = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(userToken);
        parcel.writeString(userFullName);
        parcel.writeString(userImageURL);
    }
}
