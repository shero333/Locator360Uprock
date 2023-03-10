package com.care360.findmyfamilyandfriends.Util;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.care360.findmyfamilyandfriends.databinding.LayoutCameraDialogBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutDialogAddEmergncyContactBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutGalleryDialogBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutProgressDialogBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.care360.findmyfamilyandfriends.BuildConfig;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Battery.BatteryStatusModelClass;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.JoinCircle.CircleModel;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.ContactsManually.AddContactManuallyActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.StartScreen.StartScreenActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Commons {

    private static final String TAG = "COMMONS";

    /*
        variable for handling the failed condition, if it fails is will retry till 3 times and then will
        trigger a condition showing that the image uploading failed
    */
    static int checkFailedStatus = 0;

    public static boolean validateEmailAddress(String input) {

        //enable the continue button
        //disables the continue button
        return !input.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File bitmapToFile(Context context, String currentPicturePath) {

        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(context.getExternalFilesDir("/Compressed Profile Pictures") + File.separator + System.currentTimeMillis() + ".jpg");
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(currentPicturePath);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("COMP_IMG", "exception: ", e);
            return file;
        }
    }

    public static String getRandomGeneratedCircleCode() {

        // create a string of all characters
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // 6 characters random string
        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string
        int length = 6;

        for (int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(alphabet.length());

            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }

        return sb.toString();
    }

    @SuppressLint("MissingPermission")
    public static void isGpsEnabled(Activity activity, OnSuccessListenerInterface onSuccessListenerInterface) {

        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGpsProviderEnabled) {
            onSuccessListenerInterface.onSuccess(true);
        } else {

            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.layout_gps_dialog);

            //setting the transparent background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //this sets the width of dialog to 90%
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.9);

            //setting the width and height of alert dialog
            dialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

            //click listener initialization
            AppCompatButton buttonSettings = dialog.findViewById(R.id.btn_settings_gps);

            //go to settings click listener
            buttonSettings.setOnClickListener(v -> {
                onSuccessListenerInterface.onSuccess(false);
                //dismissing the dialog
                dialog.dismiss();
            });

            dialog.setCancelable(false);
            dialog.show();

        }
    }

    public static void navigateToAppSettings(Context context) {
        context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
    }

    public static void locationPermissionDialog(Activity activity,OnSuccessListenerInterface onSuccessListenerInterface) {

        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.layout_location_dialog);

        //setting the transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        dialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        AppCompatButton buttonSettings = dialog.findViewById(R.id.btn_settings_loc_dialog);

        buttonSettings.setOnClickListener(v -> {
            onSuccessListenerInterface.onSuccess(true);
            dialog.dismiss();
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public static void cameraAndGalleryPermissionDialog(Activity activity, boolean isCameraDialog) {

        Dialog dialog = new Dialog(activity);

        if (isCameraDialog) {

            LayoutCameraDialogBinding cameraDialogBinding = LayoutCameraDialogBinding.inflate(LayoutInflater.from(activity));
            dialog.setContentView(cameraDialogBinding.getRoot());

            //button continue
            cameraDialogBinding.btnSettings.setOnClickListener(v -> {
                navigateToAppSettings(activity);
                dialog.dismiss();
            });

            //img view cancel dialog
            cameraDialogBinding.imgCancelDialog.setOnClickListener(v -> dialog.dismiss());

        }
        else {

            LayoutGalleryDialogBinding galleryDialogBinding = LayoutGalleryDialogBinding.inflate(LayoutInflater.from(activity));
            dialog.setContentView(galleryDialogBinding.getRoot());

            //button continue
            galleryDialogBinding.btnSettings.setOnClickListener(v -> {
                navigateToAppSettings(activity);
                dialog.dismiss();
            });

            //img view cancel dialog
            galleryDialogBinding.imgCancelDialog.setOnClickListener(v -> dialog.dismiss());

        }

        //setting the transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        dialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public static void signUp(Context context) {

        //initializing Firebase Auth & FireStore
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        fAuth.createUserWithEmailAndPassword(SharedPreference.getEmailPref(), SharedPreference.getPasswordPref())
                .addOnSuccessListener(authResult -> {

                    DocumentReference dr = fStore.collection(USERS_COLLECTION)
                            .document(SharedPreference.getEmailPref());

                    Map<String, Object> userInfo = new HashMap<>();

                    // array for storing circle ids in user document
                    Map<String, Object> data = new HashMap<>();
                    data.put(Constants.ID, Constants.NULL);

                    userInfo.put(Constants.FIRST_NAME, SharedPreference.getFirstNamePref());
                    userInfo.put(Constants.LAST_NAME, SharedPreference.getLastNamePref());
                    userInfo.put(Constants.PHONE_NO, SharedPreference.getPhoneNoPref());
                    userInfo.put(Constants.EMAIL, SharedPreference.getEmailPref());
                    userInfo.put(Constants.IMAGE_PATH, Constants.NULL);
                    userInfo.put(Constants.FCM_TOKEN, Constants.NULL);
                    userInfo.put(Constants.CIRCLE_IDS, data);

                    // location info fields in 'USER' collection
                    userInfo.put(Constants.LAT, 0);
                    userInfo.put(Constants.LNG, 0);
                    userInfo.put(Constants.LOC_ADDRESS, Constants.NULL);
                    userInfo.put(Constants.IS_PHONE_CHARGING, false);
                    userInfo.put(Constants.BATTERY_PERCENTAGE, 0);
                    userInfo.put(Constants.LOC_TIMESTAMP, 0);

                    dr.set(userInfo)
                            .addOnSuccessListener(unused -> Log.i(TAG, "info saved successful"))
                            .addOnFailureListener(e -> Log.e(TAG, "error saving user sign up info: " + e.getMessage()));

                    // with sign up, a FCM token will be saved with user for sending Cloud Messages Notification
                    addFCMToken();

                    //after sign up, removes the password from shared preference
                    SharedPreference.setPasswordPref(Constants.NULL);

                    Log.i(TAG, "signUp successful: ");

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "signUp failed " + e.getMessage());

                    if (e.getMessage().equals(context.getString(R.string.email_already_in_use))) {
                        Toast.makeText(context, context.getString(R.string.email_already_in_use), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Failed to Sign Up. Try Again!", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public static void uploadProfileImage(Activity activity, String currentPicturePath) {

        // renaming the file to current user email
        File oldFile = new File(currentPicturePath);

        File destDirectory = activity.getExternalFilesDir("/Renamed Profile Pictures");

        File newFile = new File(destDirectory,FirebaseAuth.getInstance().getCurrentUser().getEmail());
        boolean isFileRenamed = oldFile.renameTo(newFile);

        // if file is successfully renamed,uploads into Firebase Storage, else error
        if(isFileRenamed) {

            // saving image in Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.PROFILE_IMAGES);

            StorageReference fileRef = storageReference.child(newFile.getName());

            fileRef.putFile(Uri.fromFile(newFile))
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            //when successful; update the value of image path related field
                            Map<String, Object> imagePropertiesMap = new HashMap<>();

                            imagePropertiesMap.put(Constants.IMAGE_PATH, uri.toString());

                            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                    .update(imagePropertiesMap)
                                    .addOnSuccessListener(unused -> Log.i(TAG, "update image fields: successful"))
                                    .addOnFailureListener(e -> Log.e(TAG, "update image fields: failed" + e.getMessage()));

                        });

                        //setting the value of 'checkFailedStatus' to zero. Because the variable is static and its value can be greater than 0
                        checkFailedStatus = 0;
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Fail to upload image: " + e.getMessage());

                        //incrementing the 'checkFailedStatus' value
                        checkFailedStatus++;

                        if (checkFailedStatus < 4) {

                            if (checkFailedStatus == 3) checkFailedStatus = 0;
                            else uploadProfileImage(activity,currentPicturePath);
                        }
                    });
        }
        else {
            Log.e(TAG, "COMMONS: failed to rename file uploadProfileImage() function");
        }
    }

    public static void addFCMToken() {

        /*
            calls this function every time a user sign up or sign in to get a latest token.
            This will also handle the cases, when a token is changed.
        */

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Map<String, Object> addFCMToken = new HashMap<>();
                        addFCMToken.put(Constants.FCM_TOKEN, task.getResult());

                        // saves the token in firebase
                        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                                .document(SharedPreference.getEmailPref())
                                .update(addFCMToken)
                                .addOnSuccessListener(o -> Log.i(TAG, "addFCMToken successful"))
                                .addOnFailureListener(e -> Log.e(TAG, "addFCMToken failed: " + e.getMessage()));
                    }
                });

    }

    public static void deleteFCMToken(OnSuccessListenerInterface onSuccessListenerInterface) {

        /*
            delete the fcm when user logged out. This will help in determining the status of user (logged in/out)
            if a user has logged out of app, he will not receive FCM messages
        */

        // delete the value of specified field
        Map<String, Object> deleteFCMToken = new HashMap<>();
        deleteFCMToken.put(Constants.FCM_TOKEN, Constants.NULL);

        // update the token value in firebase
        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(SharedPreference.getEmailPref())
                .update(deleteFCMToken)
                .addOnSuccessListener(o -> {
                    Log.i(TAG, "deleteFCMToken successful");
                    onSuccessListenerInterface.onSuccess(true);
                })
                .addOnFailureListener(e -> Log.e(TAG, "deleteFCMToken failed: " + e.getMessage()));
    }


    // interface for handling the success scenarios in sign up, sign in, and FCM token deletion
    public interface OnSuccessListenerInterface {
        void onSuccess(boolean isSuccessful);
    }

    public static void signIn(Context context, String password, OnSuccessListenerInterface onSuccessListenerInterface) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(SharedPreference.getEmailPref(),password)
                .addOnSuccessListener(authResult -> onSuccessListenerInterface.onSuccess(true))
                .addOnFailureListener(e -> {

                    if(e.getMessage().contains("The password is invalid or the user does not have a password.")) {
                        Toast.makeText(context, "Error! Incorrect Password.", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "incorrect password error: " + e.getMessage());
                    }
                    else if(e.getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted.")){
                        Toast.makeText(context, "Error! Incorrect Email.", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "incorrect email error: " + e.getMessage());
                    }
                    else {
                        Toast.makeText(context, "Error! Try Again.", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "sign in error: " + e.getMessage());
                    }
                });

    }

    public static void signOut(Activity activity) {

        Commons.deleteFCMToken(isSuccessful -> {

            if(isSuccessful) {
                FirebaseAuth.getInstance().signOut();
                activity.startActivity(new Intent(activity, StartScreenActivity.class));
                activity.finish();
            }
        });

    }

    //returns true if edit text length is equal to 1
    public static boolean isEditTextLengthZero(EditText editText) {

        return editText.getText().toString().trim().length() == 1;
    }

    //function for returning the edit text values
    public static String getEditTextData(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static void clearEditTextListFocus(List<EditText> editTextList) {

        for (int i=0; i < editTextList.size(); i++) {
            editTextList.get(i).clearFocus();
        }
    }

    public interface OnCircleAvailabilityCheckListener {
        void onCircleAvailability(boolean doesCircleExist, CircleModel circleModel);
    }

    @SuppressWarnings("unchecked")
    public static void checkCircleAvailability(Context context, String enteredInviteCode, OnCircleAvailabilityCheckListener onCircleAvailabilityCheckListener) {

        FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                .whereEqualTo(Constants.CIRCLE_JOIN_CODE,enteredInviteCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot doc: queryDocumentSnapshots) {

                            List<String> circleMembersList;

                            circleMembersList = (List<String>) doc.get(Constants.CIRCLE_MEMBERS);

                            //interface calling with circle details
                            onCircleAvailabilityCheckListener.onCircleAvailability(true,
                                    new CircleModel(doc.getId(),
                                            doc.getString(Constants.CIRCLE_ADMIN),
                                            doc.getString(Constants.CIRCLE_NAME),circleMembersList, doc.getString(Constants.CIRCLE_JOIN_CODE)));
                        }
                    }
                    else {
                        Log.e(TAG, "circle does not exist");
                        Toast.makeText(context, "Error! Circle does not exist.", Toast.LENGTH_SHORT).show();

                        //calling the interface
                        onCircleAvailabilityCheckListener.onCircleAvailability(false,null);

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "join circle error: "+e.getMessage());
                    Toast.makeText(context, "Error! Try again.", Toast.LENGTH_SHORT).show();

                    //calling the interface
                    onCircleAvailabilityCheckListener.onCircleAvailability(false,null);
                });

    }

    public static BatteryStatusModelClass getCurrentBatteryStatus(Context context) {

        BatteryManager batteryManager= (BatteryManager) context.getSystemService(BATTERY_SERVICE);

        boolean isCharging = batteryManager.isCharging();
        int batteryPercentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        return new BatteryStatusModelClass(isCharging,batteryPercentage);
    }

    @SuppressLint("SimpleDateFormat")
    public static String timeInMilliToDateFormat(String timeInMilliSeconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aaa");
        return dateFormat.format(new Date(Long.parseLong(timeInMilliSeconds)));
    }

    public static String getLocationAddress(Context context, Location location) {
        String locationAddress = "";

        //getting the address of current location
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            locationAddress = addresses.get(0).getAddressLine(0);
        }

        return locationAddress;
    }

    public static void addEmergencyContactDialog(Activity activity,OnSuccessListenerInterface onSuccessListenerInterface) {

        Dialog addContactsDialog = new Dialog(activity);

        LayoutDialogAddEmergncyContactBinding dialogBinding = LayoutDialogAddEmergncyContactBinding.inflate(LayoutInflater.from(activity));
        addContactsDialog.setContentView(dialogBinding.getRoot());

        //dialog add from contact button click listener
        dialogBinding.btnSelectFromContact.setOnClickListener(view -> {
            onSuccessListenerInterface.onSuccess(true);
            addContactsDialog.dismiss();
        });

        //add contact manually
        dialogBinding.txtAddManually.setOnClickListener(view -> {
            Intent intent = new Intent(activity, AddContactManuallyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
            addContactsDialog.dismiss();
        });

        //setting the transparent background
        addContactsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        addContactsDialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        addContactsDialog.show();
    }

    public static String getContactLetters(String name) {
        String letters = "";

        for (int i = 0; i < name.length(); i++) {
            letters = Character.toString(name.charAt(0));

            if (Character.isWhitespace(name.charAt(i)) || Character.isSpaceChar(name.charAt(i))) {
                letters = letters.concat(Character.toString(name.charAt(i + 1)));
                break;
            }
        }

        return letters;
    }

    public static int randomColor() {
        return Color.argb(255, new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256));
    }

    public static void sendEmergencyContactInvitation(Context context,String phoneNumber) {
        Uri smsUri = Uri.parse("smsto:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO,smsUri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("sms_body",SharedPreference.getFullName() + " added you as Emergency contact on Find My Family app.\n" +
                "You can download " + context.getString(R.string.app_name)+" from link below:\n\n"+
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        context.startActivity(intent);
    }

    public static void currentUserFullName() {
         FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                .get()
                .addOnSuccessListener(doc -> {
                    //saving full name in shared preference
                    SharedPreference.setFullName(Objects.requireNonNull(doc.getString(Constants.FIRST_NAME)).concat(" ".concat(Objects.requireNonNull(doc.getString(Constants.LAST_NAME)))));
                });
    }

    public static Dialog progressDialog(Activity activity) {

        Dialog dialog = new Dialog(activity);

        LayoutProgressDialogBinding binding = LayoutProgressDialogBinding.inflate(activity.getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        //setting the transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 70%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.7);

        //setting the width and height of alert dialog
        dialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateFromTimeInMilli(String timeInMilliSeconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(new Date(Long.parseLong(String.valueOf(timeInMilliSeconds))));
    }

    @SuppressLint("SimpleDateFormat")
    public static String timeFromTimeInMilli(String timeInMilliSeconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");
        return dateFormat.format(new Date(Long.parseLong(String.valueOf(timeInMilliSeconds))));
    }

}
