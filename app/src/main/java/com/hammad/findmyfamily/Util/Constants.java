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
     String FCM_TOKEN = "fcm_token";
     String CIRCLE_IDS = "circle_ids";
     String ID = "id";

     // for Cloud Storage
     String PROFILE_IMAGES = "profile_images";

     String NULL = "null";

     //request codes
     int REQUEST_CODE_CAMERA = 1;
     int REQUEST_CODE_LOCATION = 2;
     int REQUEST_CODE_STORAGE = 3;

     //for circle
     String CIRCLE_COLLECTION = "Circle";
     String CIRCLE_NAME = "circle_name";
     String CIRCLE_JOIN_CODE = "circle_join_code";
     String CIRCLE_CODE_EXPIRY_DATE = "code_expiry_date";
     String CIRCLE_MEMBERS = "circle_members";
     String CIRCLE_ADMIN = "circle_admin";
     String CIRCLE_TIME_STAMP = "time_stamp";

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

     String ARE_EMERG_CONTACTS_ADDED = "are_emerg_contacts_added";

     String EMERGENCY_CONTACT = "emergency_contact";
     String CONTACT_NAME = "contact_name";
     String CONTACT_NO = "contact_no";
     String IS_CONTACT_APPROVED = "is_contact_approved";
     String OWNER_PHONE_NO = "owner_phone_no";
     String OWNER_EMAIL = "owner_email";

     String DATABASE_NAME = "FindMyFamily_db";


}
