package com.hammad.findmyfamily.HomeScreen.FragmentLocation.BottomSheetMembers;

public class MemberDetail {

    private String memberFirstName;
    private String memberLastName;
    private String memberImageUrl;
    private String locationLat;
    private String locationLng;
    private String locationAddress;
    private String locationTime;
    private boolean isPhoneCharging;
    private int batteryPercentage;

    public MemberDetail() {}

    public MemberDetail(String memberFirstName, String memberLastName, String memberImageUrl, String locationLat, String locationLng, String locationAddress, String locationTime, boolean isPhoneCharging, int batteryPercentage) {
        this.memberFirstName = memberFirstName;
        this.memberLastName = memberLastName;
        this.memberImageUrl = memberImageUrl;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.locationAddress = locationAddress;
        this.locationTime = locationTime;
        this.isPhoneCharging = isPhoneCharging;
        this.batteryPercentage = batteryPercentage;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public String getMemberImageUrl() {
        return memberImageUrl;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public boolean isPhoneCharging() {
        return isPhoneCharging;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }
}
