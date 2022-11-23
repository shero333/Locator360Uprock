package com.hammad.findmyfamily.Util;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static com.hammad.findmyfamily.Util.Constants.USERS_COLLECTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
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
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hammad.findmyfamily.BuildConfig;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.BatteryStatusModelClass;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.JoinCircle.CircleModel;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.AddContactManuallyActivity;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.StartScreen.StartScreenActivity;
import com.hammad.findmyfamily.databinding.LayoutCameraDialogBinding;
import com.hammad.findmyfamily.databinding.LayoutDialogAddEmergncyContactBinding;
import com.hammad.findmyfamily.databinding.LayoutGalleryDialogBinding;

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
import java.util.Random;

public class Commons {

    private static final String TAG = "COMMONS";

    /*
        variable for handling the failed condition, if it fails is will retry till 3 times and then will
        trigger a condition showing that the image uploading failed
    */
    static int checkFailedStatus = 0;

    public static boolean validateEmailAddress(String input) {

        if (!input.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            //enable the continue button
            return true;
        } else {
            //disables the continue button
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static File bitmapToFile(Context context, String currentPicturePath) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(context.getExternalFilesDir("/Compressed Profile Pictures") + File.separator + SharedPreference.getFirstNamePref() + "_profile_" + timeStamp + ".jpg");
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

    //rotates the bitmap by 90 degree
    public static Bitmap rotateBitmap(Bitmap source, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
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
    public static boolean isGpsEnabled(Activity activity, GetGPSListener gpsListener) {

        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGpsProviderEnabled) {
            return true;
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
            ImageFilterView filterViewExitDialog = dialog.findViewById(R.id.img_cancel_dialog);
            AppCompatButton buttonSettings = dialog.findViewById(R.id.btn_settings);

            //exit dialog click listener
            filterViewExitDialog.setOnClickListener(v -> dialog.dismiss());

            //go to settings click listener
            buttonSettings.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                gpsListener.getGPSIntent(intent);
                //dismissing the dialog
                dialog.dismiss();
            });

            dialog.setCancelable(true);
            dialog.show();

            return false;
        }
    }

    public static void navigateToAppSettings(Context context) {
        context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
    }

    public static void locationPermissionDialog(Activity activity) {

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
            navigateToAppSettings(activity);
            dialog.dismiss();
        });

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

    public static void signUp(Context context, OnSuccessListenerInterface onSuccessListenerInterface) {

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
                    userInfo.put(Constants.IMAGE_NAME, SharedPreference.getImageName());
                    userInfo.put(Constants.IMAGE_PATH, SharedPreference.getImagePath());
                    userInfo.put(Constants.FCM_TOKEN, null);
                    userInfo.put(Constants.CIRCLE_IDS, data);

                    //setting the circle info as sub-collection data
                    Map<String, Object> circleData = new HashMap<>();

                    circleData.put(Constants.CIRCLE_NAME, SharedPreference.getCircleName());
                    circleData.put(Constants.CIRCLE_JOIN_CODE, SharedPreference.getCircleInviteCode());
                    circleData.put(Constants.CIRCLE_ADMIN, SharedPreference.getEmailPref());
                    circleData.put(Constants.CIRCLE_CODE_EXPIRY_DATE, String.valueOf(System.currentTimeMillis()+259200000)); // 259200000 milliseconds = 3 days
                    circleData.put(Constants.CIRCLE_MEMBERS, FieldValue.arrayUnion(SharedPreference.getEmailPref()));

                    dr.set(userInfo);
                    dr.collection(Constants.CIRCLE_COLLECTION).add(circleData)
                            .addOnSuccessListener(documentReference -> {

                                /*
                                    when in 'Circle' sub-collection, a new document is created,
                                    its id will be stored in 'User' collection document as field. Later this ids will be used to extract Circle info
                                */

                                DocumentReference documentRefCircleId = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                                        .document(SharedPreference.getEmailPref());

                                documentRefCircleId.update(Constants.CIRCLE_IDS, FieldValue.arrayUnion(documentReference.getId()))
                                        .addOnSuccessListener(unused -> {
                                            Log.i(TAG, "CIRCLE ID: success");

                                            //after sign up, removes the password from shared preference
                                            SharedPreference.setPasswordPref(Constants.NULL);
                                        })
                                        .addOnFailureListener(e -> Log.i(TAG, "CIRCLE ID: failure = "+e.getMessage()));

                            });

                    // with sign up, a FCM token will be saved with user for sending Cloud Messages Notification
                    addFCMToken();

                    //calling the SignUpSuccessListener interface
                    onSuccessListenerInterface.onSuccess(true);

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

    public static void uploadProfileImage() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.PROFILE_IMAGES);

        StorageReference fileRef = storageReference.child(SharedPreference.getImageName());

        fileRef.putFile(Uri.fromFile(new File(SharedPreference.getImagePath())))
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        //when successful; update the values of image related field
                        Map<String, Object> imagePropertiesMap = new HashMap<>();
                        imagePropertiesMap.put(Constants.IMAGE_PATH, uri.toString());
                        imagePropertiesMap.put(Constants.IMAGE_NAME, SharedPreference.getImageName());

                        FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                                .document(SharedPreference.getEmailPref())
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
                        else uploadProfileImage();
                    }
                });
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

    public interface GetGPSListener {
        void getGPSIntent(Intent intent);
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
            activity.startActivity(new Intent(activity, AddContactManuallyActivity.class));
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

}
