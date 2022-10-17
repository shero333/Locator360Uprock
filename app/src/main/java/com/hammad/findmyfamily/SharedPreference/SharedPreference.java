package com.hammad.findmyfamily.SharedPreference;

import static com.hammad.findmyfamily.Util.Constants.EMAIL;
import static com.hammad.findmyfamily.Util.Constants.FIRST_NAME;
import static com.hammad.findmyfamily.Util.Constants.IMAGE_PATH;
import static com.hammad.findmyfamily.Util.Constants.LAST_NAME;
import static com.hammad.findmyfamily.Util.Constants.NULL;
import static com.hammad.findmyfamily.Util.Constants.PASSWORD;
import static com.hammad.findmyfamily.Util.Constants.PHONE_NO;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hammad.findmyfamily.Application.App;
import com.hammad.findmyfamily.Util.Constants;

public class SharedPreference {

    private static SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());

    public static void setPhoneNoPref(String phoneNo) {
        sharedPreference.edit().putString(Constants.PHONE_NO, phoneNo).apply();
    }

    public static String getPhoneNoPref(){
        return sharedPreference.getString(PHONE_NO,NULL);
    }

    public static void setFirstNamePref(String firstName){
        sharedPreference.edit().putString(Constants.FIRST_NAME,firstName).apply();
    }

    public static String getFirstNamePref(){
        return sharedPreference.getString(FIRST_NAME,NULL);
    }

    public static void setLastNamePref(String lastName){
        sharedPreference.edit().putString(Constants.LAST_NAME,lastName).apply();
    }

    public static String getLastNamePref(){
        return sharedPreference.getString(LAST_NAME,NULL);
    }

    public static void setEmailPref(String email){
        sharedPreference.edit().putString(Constants.EMAIL,email).apply();
    }

    public static String getEmailPref(){
        return sharedPreference.getString(EMAIL,NULL);
    }

    public static void setPasswordPref(String password){
        sharedPreference.edit().putString(Constants.PASSWORD,password).apply();
    }

    public static String getPasswordPref(){
        return sharedPreference.getString(PASSWORD,NULL);
    }

    public static void setImagePath(String imagePath){
        sharedPreference.edit().putString(Constants.IMAGE_PATH,imagePath).apply();
    }

    public static String getImagePath() {
        return sharedPreference.getString(IMAGE_PATH,NULL);
    }

    public static void setImageName(String imageName){
        sharedPreference.edit().putString(Constants.IMAGE_NAME,imageName).apply();
    }

    public static String getImageName(){
        return sharedPreference.getString(Constants.IMAGE_NAME, NULL);
    }

    public static void setCircleName(String circleName){
        sharedPreference.edit().putString(Constants.CIRCLE_NAME,circleName).apply();
    }

    public static String getCircleName(){
        return sharedPreference.getString(Constants.CIRCLE_NAME, Constants.NULL);
    }

    public static void setCircleInviteCode(String circleCode){
        sharedPreference.edit().putString(Constants.CIRCLE_JOIN_CODE,circleCode).apply();
    }

    public static String getCircleInviteCode(){
        return sharedPreference.getString(Constants.CIRCLE_JOIN_CODE, Constants.NULL);
    }

    public static void setMapType(int type) {
        sharedPreference.edit().putInt(Constants.MAP_TYPE,type).apply();
    }

    public static int getMapType() {
        return sharedPreference.getInt(Constants.MAP_TYPE,0);
    }

    private static void setCurrentCircleStatus(String circleId) {
        sharedPreference.edit().putString(Constants.CURRENT_CIRCLE,circleId).apply();
    }

    private static String getCurrentCircleStatus() {
        return sharedPreference.getString(Constants.CURRENT_CIRCLE, Constants.NULL);
    }

    private static void setFCMToken(String token) {
        sharedPreference.edit().putString(Constants.FCM_TOKEN,token).apply();
    }

    private static String getFCMToken() {
        return sharedPreference.getString(Constants.FCM_TOKEN, NULL);
    }
}
