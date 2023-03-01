package com.care360.findmyfamilyandfriends.OneTimeScreens;

import static com.care360.findmyfamilyandfriends.Util.Constants.REQUEST_CODE_CAMERA;
import static com.care360.findmyfamilyandfriends.Util.Constants.REQUEST_CODE_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.ActivityAddProfilePictureBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProfilePictureActivity extends AppCompatActivity {

    // saving the current path of image
    private static String currentPicturePath = "";

    private ActivityAddProfilePictureBinding binding;

    private static final String TAG = "ACT_ADD_PROF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize view binding
        binding = ActivityAddProfilePictureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //upload picture click listener
        binding.btnUploadImg.setOnClickListener(v -> uploadProfileImage());

        //skip to next activity
        binding.txtSkip.setOnClickListener(v -> skipProfileImage());

        //button continue click listener
        binding.btnContAddProfilePic.setOnClickListener(v -> continueButtonClickListener());
    }

    private void uploadProfileImage() {

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
                }
                else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                {
                    Toast.makeText(this, "Camera Permission Denied!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "camera permission denied");
                }
                else if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                {
                    Log.i(TAG, "camera denied and don't show again");

                    //navigate user to app settings page
                    Commons.cameraAndGalleryPermissionDialog(this,true);
                }

                break;
            }

            case REQUEST_CODE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "gallery permission allowed");
                    //opening the gallery
                    openGallery();
                }
                else if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    Toast.makeText(this, "Gallery Permission Denied!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "gallery permission denied");

                }
                else if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    Log.i(TAG, "gallery denied and don't show again");

                    //navigate user to app settings page
                    Commons.cameraAndGalleryPermissionDialog(this,false);
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
        String imageFileName = SharedPreference.getFirstNamePref() + "_" + timeStamp ;
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

            if(result.getResultCode() == Activity.RESULT_OK) {

                File file = new File(currentPicturePath);

                // file zie in KBs
                float fileSize = file.length() / 1024;

                //if file (Image) size is greater than 500 KB, it will be compressed. Otherwise, else will be called.
                if (fileSize > 300) {
                    File compressedFile = Commons.bitmapToFile(AddProfilePictureActivity.this, currentPicturePath);

                    if (compressedFile != null) {
                        Glide
                                .with(AddProfilePictureActivity.this)
                                .load(compressedFile)
                                .into(binding.imgProfile);

                        //saving the compressed file path in variable
                        currentPicturePath = compressedFile.getAbsolutePath();

                        //deletes the original camera captured image
                        file.delete();

                    }
                }
                else
                {
                    Glide
                            .with(AddProfilePictureActivity.this)
                            .load(file)
                            .into(binding.imgProfile);
                }


                //setting the CONTINUE status (enabled/disabled)
                continueButtonStatus();

            }
        }
    });

    ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK) {

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
                            File compressedFile = Commons.bitmapToFile(AddProfilePictureActivity.this, currentPicturePath);

                            if (compressedFile != null) {
                                Glide
                                        .with(AddProfilePictureActivity.this)
                                        .load(compressedFile)
                                        .into(binding.imgProfile);

                                //saving the compressed file path in variable
                                currentPicturePath = compressedFile.getAbsolutePath();
                            }
                        }
                        else {
                            Glide
                                    .with(AddProfilePictureActivity.this)
                                    .load(file)
                                    .into(binding.imgProfile);
                        }

                        //setting the CONTINUE status (enabled/disabled)
                        continueButtonStatus();

                    }
                }
            });

    private void continueButtonStatus() {
        if (currentPicturePath.length() > 0) {
            binding.btnContAddProfilePic.setEnabled(true);
            binding.btnContAddProfilePic.setBackgroundResource(R.drawable.white_rounded_button);
            binding.btnContAddProfilePic.setTextColor(getColor(R.color.orange));
        }
    }

    private void skipProfileImage() {
        //navigating to next activity
        startActivity(new Intent(this, RequestPermissionActivity.class));
    }

    private void continueButtonClickListener() {

        //uploads the image in background
        Commons.uploadProfileImage(this,currentPicturePath);

        // navigating to next screen
        startActivity(new Intent(this, RequestPermissionActivity.class));
    }

    /*private void rxJavaProfilePictureCompression(File file){


        ObservableProvider.getSmartObservable((ObservableOnSubscribe<File>) emitter -> {
            //do something
            File file1 = Commons.bitmapToFile(this, currentPicturePath);
            emitter.onNext(file1);
        }).subscribe(new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(File file) {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                //do something
            }
        });

    }*/

}