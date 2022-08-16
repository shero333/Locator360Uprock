package com.hammad.locator360.SharedPreference;

import static com.hammad.locator360.Util.Constants.EMAIL;
import static com.hammad.locator360.Util.Constants.FIRST_NAME;
import static com.hammad.locator360.Util.Constants.IMAGE_PATH;
import static com.hammad.locator360.Util.Constants.LAST_NAME;
import static com.hammad.locator360.Util.Constants.NULL;
import static com.hammad.locator360.Util.Constants.PASSWORD;
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
        return sharedPreference.getString(PHONE_NO,NULL);
    }

    public static void setFirstNamePref(String firstName){
        sharedPreference.edit().putString(FIRST_NAME,firstName).apply();
    }

    public static String getFirstNamePref(){
        return sharedPreference.getString(FIRST_NAME,NULL);
    }

    public static void setLastNamePref(String lastName){
        sharedPreference.edit().putString(LAST_NAME,lastName).apply();
    }

    public static String getLastNamePref(){
        return sharedPreference.getString(LAST_NAME,NULL);
    }

    public static void setEmailPref(String email){
        sharedPreference.edit().putString(EMAIL,email).apply();
    }

    public static String getEmailPref(){
        return sharedPreference.getString(EMAIL,NULL);
    }

    public static void setPasswordPref(String password){
        sharedPreference.edit().putString(PASSWORD,password).apply();
    }

    public static String getPasswordPref(){
        return sharedPreference.getString(PASSWORD,NULL);
    }

    public static void setImagePath(String imagePath){
        sharedPreference.edit().putString(IMAGE_PATH,imagePath).apply();
    }

    public static String getImagePath(){
        return sharedPreference.getString(IMAGE_PATH,NULL);
    }
}
