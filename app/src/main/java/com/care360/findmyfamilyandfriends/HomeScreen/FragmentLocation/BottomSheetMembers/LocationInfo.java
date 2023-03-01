package com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.BottomSheetMembers;

public class LocationInfo {

    private String lat;
    private String lng;
    private String locAddress;
    private long locTimestamp;

    public LocationInfo(String lat, String lng, String locAddress, long locTimestamp) {
        this.lat = lat;
        this.lng = lng;
        this.locAddress = locAddress;
        this.locTimestamp = locTimestamp;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLocAddress() {
        return locAddress;
    }

    public long getLocTimestamp() {
        return locTimestamp;
    }
}
