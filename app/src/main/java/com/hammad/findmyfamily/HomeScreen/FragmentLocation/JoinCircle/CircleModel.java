package com.hammad.findmyfamily.HomeScreen.FragmentLocation.JoinCircle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CircleModel implements Parcelable {

    private String circleId;
    private String circleOwnerId;
    private String circleName;
    private List<String> circleMembersList;


    public CircleModel(String circleId, String circleOwnerId, String circleName, List<String> circleMembersList) {
        this.circleId = circleId;
        this.circleOwnerId = circleOwnerId;
        this.circleName = circleName;
        this.circleMembersList = circleMembersList;
    }

    public String getCircleId() {
        return circleId;
    }

    public String getCircleOwnerId() {
        return circleOwnerId;
    }

    public String getCircleName() {
        return circleName;
    }

    public List<String> getCircleMembersList() {
        return circleMembersList;
    }

    protected CircleModel(Parcel in) {
        circleId = in.readString();
        circleOwnerId = in.readString();
        circleName = in.readString();
        circleMembersList = in.createStringArrayList();
    }

    public static final Creator<CircleModel> CREATOR = new Creator<CircleModel>() {
        @Override
        public CircleModel createFromParcel(Parcel in) {
            return new CircleModel(in);
        }

        @Override
        public CircleModel[] newArray(int size) {
            return new CircleModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(circleId);
        parcel.writeString(circleOwnerId);
        parcel.writeString(circleName);
        parcel.writeStringList(circleMembersList);
    }

}
