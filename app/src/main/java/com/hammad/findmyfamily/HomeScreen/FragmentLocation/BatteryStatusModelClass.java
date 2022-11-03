package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

public class BatteryStatusModelClass {

    private boolean isCharging;
    private int batteryPercentage;
    private boolean isPowerSavingOn;

    public BatteryStatusModelClass() {}

    public BatteryStatusModelClass(boolean isCharging, int batteryPercentage, boolean isPowerSavingOn) {
        this.isCharging = isCharging;
        this.batteryPercentage = batteryPercentage;
        this.isPowerSavingOn = isPowerSavingOn;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public boolean isPowerSavingOn() {
        return isPowerSavingOn;
    }
}
