package com.hammad.findmyfamily.Util;

public interface Constants {

     String USERS_COLLECTION = "Users";

     String PHONE_NO = "phone_no";
     String FIRST_NAME = "first_name";
     String LAST_NAME = "last_name";
     String EMAIL = "email";
     String PASSWORD = "password";
     String IMAGE_PATH = "image_path";
     String IMAGE_NAME = "image_name";

     String NULL = "null";

     //request codes
     int REQUEST_CODE_CAMERA = 1;
     int REQUEST_CODE_LOCATION = 2;
     int REQUEST_CODE_STORAGE = 3;

     //for circle
     String CIRCLE_COLLECTION = "Circle";
     String CIRCLE_NAME = "circle_name";
     String CIRCLE_CODE = "circle_code";
     String CIRCLE_CODE_EXPIRY_DATE = "code_expiry_date";
     String CIRCLE_MEMBERS = "circle_members";
     String CIRCLE_ADMIN = "circle_admin";

     //for location
     String LOCATION_COLLECTION = "Location";
     String LAT = "lat";
     String LNG = "lng";
     String LOC_DATE = "loc_date";
     String LAST_KNOWN_LOCATION = "last_known_location";
     String BATTERY_STATUS = "battery_status";

     //variables for inflating different view types in members bottom sheet
     int VIEW_TYPE_BUTTON = 1;
     int VIEW_TYPE_ITEM = 0;

     // string for navigating to OTP either from Sign Up or Reset Password scenario
     String OTP_ACT_KEY = "otp_act_key";

     String MAP_TYPE = "map_type";

     String CURRENT_CIRCLE = "current_circle";

}
