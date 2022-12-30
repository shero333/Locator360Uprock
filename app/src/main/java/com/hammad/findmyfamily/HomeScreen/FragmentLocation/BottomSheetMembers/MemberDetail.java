package com.hammad.findmyfamily.HomeScreen.FragmentLocation.BottomSheetMembers;

public class MemberDetail {

    private String memberFirstName;
    private String memberLastName;
    private String memberImageUrl;
    private String locationLat;
    private String locationLng;
    private String locationAddress;
    private /*String*//*int*/long locationTimestamp;
    private boolean isPhoneCharging;
    private int batteryPercentage;
    private String memberEmail;

    public MemberDetail() {}

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public String getMemberImageUrl() {
        return memberImageUrl;
    }

    public void setMemberImageUrl(String memberImageUrl) {
        this.memberImageUrl = memberImageUrl;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(String locationLng) {
        this.locationLng = locationLng;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public /*String*//*int*/long getLocationTimestamp() {
        return locationTimestamp;
    }

    public void setLocationTimestamp(/*String*//*int*/long locationTimestamp) {
        this.locationTimestamp = locationTimestamp;
    }

    public boolean isPhoneCharging() {
        return isPhoneCharging;
    }

    public void setPhoneCharging(boolean phoneCharging) {
        isPhoneCharging = phoneCharging;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
}
