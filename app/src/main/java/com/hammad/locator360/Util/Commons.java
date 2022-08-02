package com.hammad.locator360.Util;

import android.util.Log;
import android.util.Patterns;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

}
