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
     int REQUEST_CODE_CONTACTS = 4;

     //for circle
     String CIRCLE_COLLECTION = "Circle";
     String CIRCLE_NAME = "circle_name";
     String CIRCLE_JOIN_CODE = "circle_join_code";
     String CIRCLE_CODE_EXPIRY_DATE = "code_expiry_date";
     String CIRCLE_MEMBERS = "circle_members";
     String CIRCLE_ADMIN = "circle_admin";

     //for location
     String LOCATION_COLLECTION = "Location";
     String LAT = "lat";
     String LNG = "lng";
     String LOC_ADDRESS = "loc_address";
     String IS_PHONE_CHARGING = "is_phone_charging";
     String BATTERY_PERCENTAGE = "battery_percentage";
     String LOC_TIMESTAMP = "loc_timestamp";

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
     String OWNER_EMAIL = "owner_email";
     String CONTACT_ID = "contact_id";

     String DATABASE_NAME = "FindMyFamily_db";

     String CIRCLE_ID = "circle_id";
     String CIRCLE = "circle";

     // string for differentiating whether join circle is called during sign up or from home screen
     String CIRCLE_JOIN_ACT_KEY = "join_act_key";

     String RETURNED_CIRCLE_NAME = "circle_name";

     String ADD_MEMBER_BUTTON_CLICKED = "button_clicked";

     String IS_CIRCLE_CREATED = "is_circle_created";

}
