package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.Account;

import static com.care360.findmyfamilyandfriends.Util.Constants.REQUEST_CODE_CAMERA;
import static com.care360.findmyfamilyandfriends.Util.Constants.REQUEST_CODE_STORAGE;
import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.care360.findmyfamilyandfriends.databinding.ActivityProfileInfoUpdateBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileInfoActivity extends AppCompatActivity {

    private static final String TAG = "UPDATE_PROFILE_INFO_ACT";

    ActivityProfileInfoUpdateBinding binding;

    UserDetail userDetail;

    // saving the current path of image
    private String currentPicturePath = Constants.NULL;

    private String firstName, lastName;
    private boolean isFirstNameEntered = true, isLastNameEntered = true;

    private Dialog dialog;
    private TemplateView template;
    private AdLoader adloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityProfileInfoUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this);

        template = binding.adTemplate;

        adloader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {

                    template.setStyles(new NativeTemplateStyle.Builder().build());
                    template.setNativeAd(nativeAd);

                })
                .build();

        adloader.loadAd(new AdRequest.Builder().build());


        // get intent data
        getIntentData();

        //toolbar back pressed
        binding.toolbarEditProfile.setNavigationOnClickListener(v -> onBackPressed());

        // image view click listener
        binding.imageProfile.setOnClickListener(v -> selectProfileImage());

        // text watchers
        binding.edtTxtFirstName.addTextChangedListener(firstNameTextWatcher);

        binding.edtTxtLastName.addTextChangedListener(lastNameTextWatcher);

        updateButtonStatus();

        // button update click listener
        binding.btnUpdate.setOnClickListener(v -> buttonUpdateClickListener());
    }

    private void getIntentData() {

        Intent intent = getIntent();

        if (intent != null) {

            userDetail = intent.getParcelableExtra(Constants.USER_INFO);

            if (userDetail.getImagePath().equals(Constants.NULL)) {
                binding.imageProfile.setBackgroundColor(intent.getIntExtra(Constants.RANDOM_COLOR, -1));

                binding.textNameLetter.setVisibility(View.VISIBLE);
                binding.textNameLetter.setText(String.valueOf(userDetail.getFirstName().charAt(0)));
            } else if (!userDetail.getImagePath().equals(Constants.NULL)) {
                binding.textNameLetter.setVisibility(View.GONE);

                //load profile image
                Glide
                        .with(this)
                        .load(userDetail.getImagePath())
                        .into(binding.imageProfile);
            }

            binding.edtTxtFirstName.setText(userDetail.getFirstName());
            binding.edtTxtLastName.setText(userDetail.getLastName());

            // saving the values in string variables
            firstName = userDetail.getFirstName();
            lastName = userDetail.getLastName();
        }
    }

    private void updateButtonStatus() {
        //if true, enables the continue button
        if (isFirstNameEntered && isLastNameEntered) {

            binding.btnUpdate.setEnabled(true);
            binding.btnUpdate.setBackgroundResource(R.drawable.orange_rounded_button);
            binding.btnUpdate.setTextColor(getColor(R.color.white));
        } else if (!isFirstNameEntered || !isLastNameEntered) {
            binding.btnUpdate.setEnabled(false);
            binding.btnUpdate.setBackgroundResource(R.drawable.disabled_round_button);
            binding.btnUpdate.setTextColor(getColor(R.color.orange));
        }
    }

    private final TextWatcher firstNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            firstName = charSequence.toString().trim();

            if (firstName.length() > 0) {
                isFirstNameEntered = true;
            } else if (firstName.length() == 0) {
                isFirstNameEntered = false;
            }

            /*
                function for checking the length of both edit texts (first & last name)
                If length of both are greater than zero, enables the 'update' button
            */
            updateButtonStatus();
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private final TextWatcher lastNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            lastName = charSequence.toString().trim();

            if (lastName.length() > 0) {
                isLastNameEntered = true;
            } else if (lastName.length() == 0) {
                isLastNameEntered = false;
            }

            /*
                function for checking the length of both edit texts (first & last name)
                If length of both are greater than zero, enables the 'update' button
            */
            updateButtonStatus();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void buttonUpdateClickListener() {

        // showing dialog
        dialog = Commons.progressDialog(this);

        Map<String, Object> data = new HashMap<>();

        data.put(Constants.FIRST_NAME, firstName);
        data.put(Constants.LAST_NAME, lastName);

        // no image is select to update
        if (currentPicturePath.equals(Constants.NULL)) {

            currentPicturePath = userDetail.getImagePath();

            data.put(Constants.IMAGE_PATH, currentPicturePath);

            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .update(data)
                    .addOnSuccessListener(unused -> {

                        // hides progress dialog
                        dialog.dismiss();

                        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        // hides progress dialog
                        dialog.dismiss();

                        Log.e(TAG, "failed to update profile " + e.getMessage());

                        Toast.makeText(this, "Error! Failed to update profile.", Toast.LENGTH_LONG).show();
                    });
        }
        else if(!currentPicturePath.equals(Constants.NULL)) {

            // renaming the file to current user email
            File oldFile = new File(currentPicturePath);

            File destDirectory = getExternalFilesDir("/Renamed Profile Pictures");

            File newFile = new File(destDirectory,FirebaseAuth.getInstance().getCurrentUser().getEmail());
            boolean isFileRenamed = oldFile.renameTo(newFile);

            // if file is successfully renamed,uploads into Firebase Storage, else error
            if(isFileRenamed) {
                // uploading image to storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.PROFILE_IMAGES);

                StorageReference fileRef = storageReference.child(newFile.getName());

                fileRef.putFile(Uri.fromFile(newFile))
                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            data.put(Constants.IMAGE_PATH, uri.toString());

                            FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                    .update(data)
                                    .addOnSuccessListener(unused -> {
                                        dialog.dismiss();
                                        Log.i(TAG, "profile with image updated successfully");
                                        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Log.e(TAG, "update profile error" + e.getMessage());
                                        Toast.makeText(this, "Error! Failed to Updated Profile.", Toast.LENGTH_LONG).show();
                                    });

                        }))
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Log.e(TAG, "error updating profile with picture: " + e.getMessage());
                            Toast.makeText(this, "Error! Failed to Updated Profile.", Toast.LENGTH_LONG).show();
                        });
            }
            else {
                Log.e(TAG, "failed to renamed file");

                dialog.dismiss();
                Toast.makeText(this, "Error! Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectProfileImage() {

        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Style_Round_Dialog_Corner);

        builder.setTitle("Add Profile Photo");
        builder.setCancelable(false);

        builder.setItems(options, (dialogInterface, i) -> {

            if (options[i].equals("Camera")) {
                checkCameraPermission();
            } else if (options[i].equals("Gallery")) {
                checkGalleryPermission();
            } else if (options[i].equals("Cancel")) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void checkCameraPermission() {

        if (Permissions.hasCameraPermission(this)) {
            //opening the camera
            openCamera();
        } else {
            //requesting the Camera permission
            Permissions.getCameraPermission(this);
        }

    }

    private void checkGalleryPermission() {

        if (Permissions.hasStoragePermission(this)) {
            //opening the gallery
            openGallery();
        } else {
            //requesting the Gallery permission
            Permissions.getStoragePermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "camera permission allowed");
                    //opening the camera
                    openCamera();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera Permission Denied!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "camera permission denied");
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Log.i(TAG, "camera denied and don't show again");

                    //navigate user to app settings page
                    Commons.cameraAndGalleryPermissionDialog(this, true);
                }

                break;
            }

            case REQUEST_CODE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "gallery permission allowed");
                    //opening the gallery
                    openGallery();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Gallery Permission Denied!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "gallery permission denied");

                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.i(TAG, "gallery denied and don't show again");

                    //navigate user to app settings page
                    Commons.cameraAndGalleryPermissionDialog(this, false);
                }

                break;
            }

        }
    }

    //take image from camera
    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = SharedPreference.getFirstNamePref() + "_" + timeStamp;
        File storageDir = this.getExternalFilesDir("/Camera Images");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPicturePath = image.getAbsolutePath();
        return image;
    }

    private void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.care360.android.findmyfamilyandfriends",
                        photoFile);

                Log.i(TAG, "uri: " + photoURI);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraResultLauncher.launch(takePictureIntent);

            }
        }
    }

    //choose image from gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryResultLauncher.launch(galleryIntent);
    }

    private String getPathFromUri(Uri selectionImageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(selectionImageUri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        File file = new File(currentPicturePath);

                        // file zie in KBs
                        float fileSize = file.length() / 1024;

                        //if file (Image) size is greater than 500 KB, it will be compressed. Otherwise, else will be called.
                        if (fileSize > 300) {
                            File compressedFile = Commons.bitmapToFile(UpdateProfileInfoActivity.this, currentPicturePath);

                            if (compressedFile != null) {
                                Glide
                                        .with(UpdateProfileInfoActivity.this)
                                        .load(compressedFile.getAbsolutePath())
                                        .into(binding.imageProfile);

                                //saving the compressed file path in variable
                                currentPicturePath = compressedFile.getAbsolutePath();

                                //deletes the original camera captured image
                                file.delete();

                                //hiding the name characters textview
                                binding.textNameLetter.setVisibility(View.GONE);
                            }
                        } else {
                            Glide
                                    .with(UpdateProfileInfoActivity.this)
                                    .load(file.getAbsolutePath())
                                    .into(binding.imageProfile);

                            //hiding the name characters textview
                            binding.textNameLetter.setVisibility(View.GONE);
                        }

                    }
                }
            });
    
    ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Uri contentUri = null;
                        if (result.getData() != null) {
                            contentUri = result.getData().getData();
                        }

                        currentPicturePath = getPathFromUri(contentUri);

                        File file = new File(currentPicturePath);

                        //file size in KBs
                        float fileSize = file.length() / 1024;

                        //if file (Image) size is greater than 500 KB, it will be compressed. Otherwise, else will be called.
                        if (fileSize > 300) {
                            File compressedFile = Commons.bitmapToFile(UpdateProfileInfoActivity.this, currentPicturePath);

                            if (compressedFile != null) {

                                Glide
                                        .with(UpdateProfileInfoActivity.this)
                                        .load(compressedFile)
                                        .into(binding.imageProfile);

                                //saving the compressed file path in variable
                                currentPicturePath = compressedFile.getAbsolutePath();

                                //hiding the name characters textview
                                binding.textNameLetter.setVisibility(View.GONE);
                                Log.i(TAG, "gallery result launcher IF called: "+currentPicturePath);
                            }
                        }
                        else {
                            Glide
                                    .with(UpdateProfileInfoActivity.this)
                                    .load(file)
                                    .into(binding.imageProfile);

                            Log.i(TAG, "gallery result launcher ELSE called: "+currentPicturePath);

                            //hiding the name characters textview
                            binding.textNameLetter.setVisibility(View.GONE);
                        }
                    }
                }
            });

}