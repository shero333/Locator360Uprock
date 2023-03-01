package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Battery;

public class BatteryStatusModelClass {

    private boolean isCharging;
    private int batteryPercentage;

    public BatteryStatusModelClass(boolean isCharging, int batteryPercentage) {
        this.isCharging = isCharging;
        this.batteryPercentage = batteryPercentage;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

}
