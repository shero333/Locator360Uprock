package com.hammad.locator360.SharedPreference;

import static com.hammad.locator360.Util.Constants.PHONE_NO;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hammad.locator360.Application.App;

public class SharedPreference {

    private static SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());

    public static void setPhoneNoPref(String phoneNo) {
        sharedPreference.edit().putString(PHONE_NO, phoneNo).apply();
    }

    public static String getPhoneNoPref(){
        return sharedPreference.getString(PHONE_NO,"null");
    }
}
