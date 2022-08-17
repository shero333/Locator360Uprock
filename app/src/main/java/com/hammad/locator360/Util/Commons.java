package com.hammad.locator360.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.util.Patterns;

import com.hammad.locator360.SharedPreference.SharedPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        } else {
            //disables the continue button
            return false;
        }
    }

    public static File bitmapToFile(Context context,String currentPicturePath,boolean isCameraImage) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(context.getExternalFilesDir("/Compressed Profile Pictures") + File.separator + SharedPreference.getFirstNamePref()+"_profile_"+timeStamp+".jpg");
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(currentPicturePath);

            //if image to be compressed is of camera, then will rotate the bitmap (90 degree)
            if(isCameraImage){
                rotateBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, 50 , bos);
            }
            else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50 , bos);
            }

            byte[] bitmapData = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return file;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("COMP_IMG", "exception: ", e);
            // in case of exception, it will return null
            return file;
        }
    }

    //rotates the bitmap by 90 degree
    public static Bitmap rotateBitmap(Bitmap source)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
