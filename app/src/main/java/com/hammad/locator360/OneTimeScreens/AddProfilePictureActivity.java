package com.hammad.locator360.OneTimeScreens;

import static com.hammad.locator360.Util.Constants.NULL;
import static com.hammad.locator360.Util.Constants.REQUEST_CODE_CAMERA;
import static com.hammad.locator360.Util.Constants.REQUEST_CODE_STORAGE;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.hammad.locator360.Permission.Permissions;
import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.Util.Commons;
import com.hammad.locator360.databinding.ActivityAddProfilePictureBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProfilePictureActivity extends AppCompatActivity {

    private ActivityAddProfilePictureBinding binding;

    // saving the current path of image
    private static String currentPicturePath = "";

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
        binding.btnContAddProfilePic.setOnClickListener(v -> saveImagePathToPref());
    }

    private void uploadProfileImage() {

        final CharSequence[] options = {"Camera" ,"Gallery","Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Style_Round_Dialog_Corner);

        builder.setTitle("Add Profile Photo");
        builder.setCancelable(false);

        builder.setItems(options, (dialogInterface, i) -> {

            if(options[i].equals("Camera")){
                checkCameraPermission();
            }
            else if(options[i].equals("Gallery")) {
               checkGalleryPermission();
            }
            else if(options[i].equals("Cancel")) {
                dialogInterface.dismiss();
            }

        });

        builder.show();

    }

    private void checkCameraPermission() {

        if(Permissions.hasCameraPermission(this)) {
            //opening the camera
            openCamera();
        }
        else {
            //requesting the Camera permission
            Permissions.getCameraPermission(this);
        }

    }

    private void checkGalleryPermission() {

        if(Permissions.hasStoragePermission(this)){
            //opening the gallery
            openGallery();
        }
        else {
            //requesting the Gallery permission
            Permissions.getStoragePermission(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case REQUEST_CODE_CAMERA:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //opening the camera
                    openCamera();
                }
                else {
                    Toast.makeText(this, "Camera Permission Denied!", Toast.LENGTH_SHORT).show();
                }

                break;
            }

            case REQUEST_CODE_STORAGE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //opening the gallery
                    openGallery();
                }
                else {
                    Toast.makeText(this, "Gallery Permission Denied!", Toast.LENGTH_SHORT).show();
                }

                break;
            }

        }
    }

    //take image from camera
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
                        "com.hammad.android.findmyfamilyfileprovider",
                        photoFile);

                Log.i("IMAGE_FILE", "uri: "+photoURI);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);

            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
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

    //choose image from gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_STORAGE);
    }

    private String getPathFromUri(Uri selectionImageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(selectionImageUri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {

                File file = new File(currentPicturePath);

                //Glide library used here to image loading etc smooth & fast
                Glide
                        .with(this)
                        .load(file)
                        .into(binding.imgProfile);

                // file zie in KBs
                float fileSize = file.length()/1024;

                //file object for storing the compressed image
                File compressedFile = null;

                //if file (Image) size is greater than 500 KB, it will be compressed
                if(fileSize > 500)
                {
                    compressedFile = Commons.bitmapToFile(this,currentPicturePath,true);
                }

                if(compressedFile != null){
                    Glide
                            .with(this)
                            .load(compressedFile)
                            .into(binding.imgProfile);

                    Log.i("IMAGE_FILE", "compressed camera path: "+compressedFile.getAbsolutePath());

                    //deletes the original camera captured image
                    file.delete();
                }

                //setting the CONTINUE status (enabled/disabled)
                continueButtonStatus();
            }
        }

        //selecting image from gallery
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri contentUri = data.getData();

                currentPicturePath = getPathFromUri(contentUri);

                File file=new File(currentPicturePath);

                //Glide library used here to image loading etc smooth & fast
                Glide
                        .with(this)
                        .load(file)
                        .into(binding.imgProfile);

                //file size in KBs
                float fileSize = file.length()/1024;

                File compressedFile = null;

                if(fileSize > 500)
                {
                    compressedFile = Commons.bitmapToFile(this,currentPicturePath,false);
                }

                if(compressedFile != null){
                    Glide
                            .with(this)
                            .load(compressedFile)
                            .into(binding.imgProfile);

                    Log.i("IMAGE_FILE", "compressed gallery path: "+compressedFile.getAbsolutePath());
                }

                //setting the CONTINUE status (enabled/disabled)
                continueButtonStatus();
            }
        }
    }

    private void continueButtonStatus(){
        if(currentPicturePath.length() > 0){
            binding.btnContAddProfilePic.setEnabled(true);
            binding.btnContAddProfilePic.setBackgroundResource(R.drawable.white_rounded_button);
            binding.btnContAddProfilePic.setTextColor(getResources().getColor(R.color.orange));
        }

    }

    private void skipProfileImage(){

        //setting the null value in image path preference
        SharedPreference.setImagePath(NULL);

        //navigating to next activity
        startActivity(new Intent(this,RequestPermissionActivity.class));
    }

    private void saveImagePathToPref(){

        //saving the selected image path to preference
        SharedPreference.setImagePath(currentPicturePath);

        //navigating to next activity
        startActivity(new Intent(this,RequestPermissionActivity.class));
    }

}