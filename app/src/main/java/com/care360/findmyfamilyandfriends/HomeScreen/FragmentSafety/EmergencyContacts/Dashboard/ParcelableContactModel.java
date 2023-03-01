package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableContactModel implements Parcelable {

    private String parOwnerEmail;
    private String parContactId;
    private String parContactName;
    private String parContactNumber;

    public ParcelableContactModel(String parOwnerEmail, String parContactId, String parContactName, String parContactNumber) {
        this.parOwnerEmail = parOwnerEmail;
        this.parContactId = parContactId;
        this.parContactName = parContactName;
        this.parContactNumber = parContactNumber;
    }

    public ParcelableContactModel() {}

    public String getParOwnerEmail() {
        return parOwnerEmail;
    }

    public String getParContactId() {
        return parContactId;
    }

    public String getParContactName() {
        return parContactName;
    }

    public String getParContactNumber() {
        return parContactNumber;
    }

    protected ParcelableContactModel(Parcel in) {
        parOwnerEmail = in.readString();
        parContactId = in.readString();
        parContactName = in.readString();
        parContactNumber = in.readString();
    }

    public static final Creator<ParcelableContactModel> CREATOR = new Creator<ParcelableContactModel>() {
        @Override
        public ParcelableContactModel createFromParcel(Parcel in) {
            return new ParcelableContactModel(in);
        }

        @Override
        public ParcelableContactModel[] newArray(int size) {
            return new ParcelableContactModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(parOwnerEmail);
        parcel.writeString(parContactId);
        parcel.writeString(parContactName);
        parcel.writeString(parContactNumber);
    }
}
