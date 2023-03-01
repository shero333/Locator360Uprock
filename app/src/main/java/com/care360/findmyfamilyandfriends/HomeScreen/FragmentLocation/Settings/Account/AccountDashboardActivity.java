package com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.Account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.JoinCircle.CircleModel;
import com.care360.findmyfamilyandfriends.ResetPassword.ByPhoneNo.ResetPasswordPhoneActivity;
import com.care360.findmyfamilyandfriends.StartScreen.StartScreenActivity;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityAccountDashboardBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ACCOUNT_DASHBOARD_ACT";

    ActivityAccountDashboardBinding binding;

    UserDetail userDetail;

    int randomImageBackgroundColor;

    List<CircleModel> circleModelList = new ArrayList<>();

    Dialog progressDialog;

    List<String> allCircleDocsIdList = new ArrayList<>();

    List<String> allLocationDocsIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityAccountDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarAccount.setNavigationOnClickListener(v -> onBackPressed());

        // profile click
        binding.consProfile.setOnClickListener(v -> {

            Intent intent = new Intent(this,UpdateProfileInfoActivity.class);
            intent.putExtra(Constants.USER_INFO,userDetail);
            intent.putExtra(Constants.RANDOM_COLOR,randomImageBackgroundColor);
            startActivity(intent);
        });

        // update phone number
        binding.textEditPhoneNo.setOnClickListener(v -> {
            Intent intent = new Intent(this,UpdatePhoneNoActivity.class);
            intent.putExtra(Constants.PHONE_NO,userDetail.getPhoneNumber());
            startActivity(intent);
        });

        //update password
        binding.textChangePassword.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordPhoneActivity.class)));

        // delete account
        binding.textDeleteAccount.setOnClickListener(v -> deleteAccount());

        // get all the document ids of 'Circle' sub-collection & 'Location' sub-collection
        getAllDocsId();
    }

    private void getAllDocsId() {

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // all circle document ids
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUserEmail)
                .collection(Constants.CIRCLE_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot doc: queryDocumentSnapshots) {
                        allCircleDocsIdList.add(doc.getId());
                    }
                    Log.i(TAG, "circle docs list size: "+allCircleDocsIdList.size());

                })
                .addOnFailureListener(e -> Log.i(TAG, "circle docs list error: "+e.getMessage()));

        // all location document ids
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUserEmail)
                .collection(Constants.LOCATION_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot doc: queryDocumentSnapshots) {
                        allLocationDocsIdList.add(doc.getId());
                    }
                    Log.i(TAG, "location docs list size: "+allLocationDocsIdList.size());

                })
                .addOnFailureListener(e -> Log.i(TAG, "location docs list error: "+e.getMessage()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //getting the user info from firebase
        getUserInfo();
    }

    private void getUserInfo() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(doc -> {

                    userDetail = new UserDetail(doc.getString(Constants.FIRST_NAME), doc.getString(Constants.LAST_NAME)
                            ,doc.getString(Constants.PHONE_NO), doc.getString(Constants.IMAGE_PATH));

                    randomImageBackgroundColor = Commons.randomColor();

                    //set data to views
                    if(userDetail.getImagePath().equals(Constants.NULL)) {
                        binding.textNameLetter.setVisibility(View.VISIBLE);
                        binding.textNameLetter.setText(String.valueOf(userDetail.getFirstName().charAt(0)));

                        //setting random generated color to image view background
                        binding.imageProfile.setBackgroundColor(randomImageBackgroundColor);
                    }
                    else if(!userDetail.getImagePath().equals(Constants.NULL)) {
                        binding.textNameLetter.setVisibility(View.GONE);

                        Glide.with(this)
                                .load(userDetail.getImagePath())
                                .into(binding.imageProfile);
                    }

                    binding.textUserFullName.setText(userDetail.getFirstName().concat(" ".concat(userDetail.getLastName())));


                })
                .addOnFailureListener(e -> Log.e(TAG, "error getting user info: "+e.getMessage()));
    }

    @SuppressWarnings("unchecked")
    private void deleteAccount() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false)
                .setTitle("Delete Account?")
                .setMessage("Do you want to delete your account along with the saved data?")
                .setCancelable(false)
                .setPositiveButton("Delete", (dialogInterface, i) -> {

                    // loads and show progress dialog
                    progressDialog = Commons.progressDialog(this);

                    // deleting the profile image from storage (if any)
                    if(!userDetail.getImagePath().equals(Constants.NULL))
                    {
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(userDetail.getImagePath());
                        storageReference.delete().addOnSuccessListener(unused -> Log.i(TAG, "image delete successful"))
                        .addOnFailureListener(e -> Log.e(TAG, "error in image deletion: "+e.getMessage()));
                    }

                    // current user email
                    String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    // circles returned where user is a member
                    FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                            .whereArrayContains(Constants.CIRCLE_MEMBERS,currentUserEmail)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots ->
                            {
                                for(DocumentSnapshot doc: queryDocumentSnapshots)
                                {
                                    circleModelList.add(new CircleModel(doc.getId(), Objects.requireNonNull(doc.get(Constants.CIRCLE_ADMIN)).toString(), doc.getString(Constants.CIRCLE_NAME),
                                            (List<String>) doc.get(Constants.CIRCLE_MEMBERS), doc.getString(Constants.CIRCLE_JOIN_CODE)));
                                }

                                // list of circle user has joined
                                List<CircleModel> userIsMemberCircleList = new ArrayList<>();

                                for(int j=0; j < circleModelList.size(); j++)
                                {
                                    if(!circleModelList.get(j).getCircleOwnerId().equals(currentUserEmail))
                                    {
                                        userIsMemberCircleList.add(circleModelList.get(j));
                                    }
                                }

                                deleteUserAccountInfo(userIsMemberCircleList);

                            });
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.create().show();

    }

    private void deleteUserAccountInfo(List<CircleModel> userIsMemberCircleList) {

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // batch write object
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // removes the current user from any joined circle (circles other than created by current user himself)
        for(int i=0;  i < userIsMemberCircleList.size(); i++)
        {
            DocumentReference dr = FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                    .document(userIsMemberCircleList.get(i).getCircleOwnerId())
                    .collection(Constants.CIRCLE_COLLECTION)
                    .document(userIsMemberCircleList.get(i).getCircleId());

            batch.update(dr,Constants.CIRCLE_MEMBERS,FieldValue.arrayRemove(currentUserEmail));
        }

        // delete current user all circles
        for(int k=0; k < allCircleDocsIdList.size(); k++) {

            DocumentReference dr = FirebaseFirestore.getInstance()
                                    .collection(Constants.USERS_COLLECTION)
                                    .document(currentUserEmail)
                                    .collection(Constants.CIRCLE_COLLECTION)
                                    .document(allCircleDocsIdList.get(k));

            batch.delete(dr);
        }

        // delete current user all locations
        for(int l=0; l < allLocationDocsIdList.size(); l++) {

            DocumentReference dr = FirebaseFirestore.getInstance()
                                    .collection(Constants.USERS_COLLECTION)
                                    .document(currentUserEmail)
                                    .collection(Constants.LOCATION_COLLECTION)
                                    .document(allLocationDocsIdList.get(l));

            WriteBatch locationBatch = FirebaseFirestore.getInstance().batch();



            locationBatch.delete(dr).commit()
                    .addOnSuccessListener(unused -> Log.i(TAG, "locations deleted successfully: "))
                    .addOnFailureListener(e -> Log.i(TAG, "error deleting firebase location data: "+e.getMessage()));
        }

        // delete user info document
        DocumentReference dr = FirebaseFirestore.getInstance()
                                    .collection(Constants.USERS_COLLECTION)
                                    .document(currentUserEmail);

        // write batch
        batch
                .delete(dr)
                .commit()
                .addOnSuccessListener(unused -> {
                    Log.i(TAG, "delete user account successful");

                    // dismiss progress dialog
                    progressDialog.dismiss();

                    // signs out the current user
                    FirebaseAuth.getInstance().signOut();

                    startActivity(new Intent(this, StartScreenActivity.class));

                    Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "error deleting user account: " + e.getMessage());

                    // dismiss the progress dialog
                    progressDialog.dismiss();

                    Toast.makeText(this, "Error! Failed to Delete Account.", Toast.LENGTH_LONG).show();
                });
    }

}