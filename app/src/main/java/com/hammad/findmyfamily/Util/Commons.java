package com.hammad.findmyfamily.Util;

import static com.hammad.findmyfamily.Util.Constants.USERS_COLLECTION;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Commons {

    /*
        These two functions {encryptedText(String text), bytesToHex(byte[] hash)} are used for encryption in SHA-256 hash code. It remain same for a particular group of text.
        We will save this encrypted text in firebase, and when user enters password, we will convert it into Hex and then compare with the firebase password.
    */
    public static String encryptedText(String text) {
        MessageDigest digest;
        String encryptedText = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));

            encryptedText = bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            Log.e("ERROR_COMMONS", "NoSuchAlgorithmException " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.e("ERROR_COMMONS", "UnsupportedEncodingException " + e.getMessage());
            e.printStackTrace();

        }

        return encryptedText;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean validateEmailAddress(String input) {
        if (!input.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            //enable the continue button
            return true;
        }
        else {
            //disables the continue button
            return false;
        }
    }

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
        }
        catch (Exception e) {
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

    public static void signUp(Context context) {

        //initializing Firebase Auth & FireStore
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        fAuth.createUserWithEmailAndPassword(SharedPreference.getEmailPref(), SharedPreference.getPasswordPref())
                .addOnSuccessListener(authResult -> {

                    FirebaseUser firebaseUser = fAuth.getCurrentUser();

                    DocumentReference dr = fStore.collection(USERS_COLLECTION)
                            .document(/*firebaseUser.getEmail()*/SharedPreference.getEmailPref());

                    Map<String, Object> userInfo = new HashMap<>();

                    userInfo.put(Constants.FIRST_NAME, SharedPreference.getFirstNamePref());
                    userInfo.put(Constants.LAST_NAME, SharedPreference.getLastNamePref());
                    userInfo.put(Constants.PHONE_NO, SharedPreference.getPhoneNoPref());
                    userInfo.put(Constants.EMAIL, SharedPreference.getEmailPref());
                    userInfo.put(Constants.PASSWORD, SharedPreference.getPasswordPref());
                    userInfo.put(Constants.IMAGE_NAME, SharedPreference.getImageName());
                    userInfo.put(Constants.IMAGE_PATH, SharedPreference.getImagePath());

                    dr.set(userInfo);

                    Toast.makeText(context, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error! Failed to Sign Up.", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_CREATE_PSS_ACT", "signUp failed " + e.getMessage());
                });

    }

}
