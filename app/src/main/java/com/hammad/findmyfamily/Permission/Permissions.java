package com.hammad.findmyfamily.Permission;

import static com.hammad.findmyfamily.Util.Constants.REQUEST_CODE_CAMERA;
import static com.hammad.findmyfamily.Util.Constants.REQUEST_CODE_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hammad.findmyfamily.Util.Constants;

public class Permissions {

    public static boolean hasCameraPermission(Context context){
        String permission = Manifest.permission.CAMERA;

        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getCameraPermission(Activity activity){
        activity.requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
    }

    public static boolean hasStoragePermission(Context context){

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        return context.checkCallingOrSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                context.checkCallingOrSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED ;
    }

    public static void getStoragePermission(Activity activity){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        activity.requestPermissions(permissions,REQUEST_CODE_STORAGE);
    }

    public static boolean hasLocationPermission(Context context) {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION};

        return  context.checkCallingOrSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                context.checkCallingOrSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                context.checkCallingOrSelfPermission(permissions[2]) == PackageManager.PERMISSION_GRANTED ;
    }

    public static void getLocationPermission(Activity activity){
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        activity.requestPermissions(permissions, Constants.REQUEST_CODE_LOCATION);
    }

    public static boolean hasContactPermission(Context context) {

        String permission = Manifest.permission.READ_CONTACTS;

        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getContactPermission(Activity activity) {
        activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},Constants.REQUEST_CODE_CONTACTS);
    }

    public static boolean hasSmsPermission(Context context) {
        String permission = Manifest.permission.SEND_SMS;
        return context.checkCallingOrSelfPermission(permission)  == PackageManager.PERMISSION_GRANTED;
    }

}
