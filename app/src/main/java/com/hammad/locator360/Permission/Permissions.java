package com.hammad.locator360.Permission;

import static com.hammad.locator360.Util.Constants.REQUEST_CODE_CAMERA;
import static com.hammad.locator360.Util.Constants.REQUEST_CODE_FINE_LOCATION;
import static com.hammad.locator360.Util.Constants.REQUEST_CODE_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

public class Permissions {

    public static boolean hasCameraPermission(Context context){
        String permission = "Manifest.permission.CAMERA";

        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getCameraPermission(Activity activity){
        activity.requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
    }

    public static boolean hasStoragePermission(Context context){

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        return context.checkCallingOrSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                context.checkCallingOrSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getStoragePermission(Activity activity){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        activity.requestPermissions(permissions,REQUEST_CODE_STORAGE);
    }

    public static boolean hasLocationPermission(Context context){
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

        return context.checkCallingOrSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                context.checkCallingOrSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getLocationPermission(Activity activity){
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

        activity.requestPermissions(permissions,REQUEST_CODE_FINE_LOCATION);
    }
}
