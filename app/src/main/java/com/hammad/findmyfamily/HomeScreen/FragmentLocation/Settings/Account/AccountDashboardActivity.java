package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.Account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.JoinCircle.CircleModel;
import com.hammad.findmyfamily.ResetPassword.ByPhoneNo.ResetPasswordPhoneActivity;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityAccountDashboardBinding;

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
                    //progressDialog = Commons.progressDialog(this);

                    String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    // code for deleting the value from any joined circle
                    FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                            .whereArrayContains(Constants.CIRCLE_MEMBERS,currentUserEmail)
                            .addSnapshotListener((value, error) ->
                            {
                                for(DocumentSnapshot doc: value)
                                {
                                    circleModelList.add(new CircleModel(doc.getId(), Objects.requireNonNull(doc.get(Constants.CIRCLE_ADMIN)).toString(), doc.getString(Constants.CIRCLE_NAME),
                                            (List<String>) doc.get(Constants.CIRCLE_MEMBERS), doc.getString(Constants.CIRCLE_JOIN_CODE)));
                                }
                                Log.i(TAG, "circle list: "+circleModelList.size());

                                List<CircleModel> filteredCircleList = new ArrayList<>();
                                for(int j=0; j< circleModelList.size();j++) {

                                    if(!circleModelList.get(j).getCircleOwnerId().equals(currentUserEmail)) {
                                        filteredCircleList.add(circleModelList.get(j));
                                    }
                                    Log.i(TAG, "circle id: "+circleModelList.get(j).getCircleId());
                                    Log.i(TAG, "circle name: "+circleModelList.get(j).getCircleName());
                                    Log.i(TAG, "admin id: "+circleModelList.get(j).getCircleOwnerId());
                                    Log.i(TAG, "join code: "+circleModelList.get(j).getCircleJoinCode());
                                    Log.i(TAG, "members list "+j);

                                    for(int k=0; k < circleModelList.get(j).getCircleMembersList().size(); k++) {
                                        Log.i(TAG, "member: "+circleModelList.get(j).getCircleMembersList().get(k));
                                    }
                                }

                                deletIdFromCircles(filteredCircleList);
                            });

                    // deleting the profile image from storage if any
                    if(!userDetail.getImagePath().equals(Constants.NULL)) {
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(userDetail.getImagePath());
                        storageReference.delete().addOnSuccessListener(unused -> Log.i(TAG, "image delete successful"))
                        .addOnFailureListener(e ->Log.i(TAG, "error in image deletion: "+e.getMessage()));
                    }



                    // for deleting collection CollectionReference

                    /*WriteBatch batch = FirebaseFirestore.getInstance().batch();

                         DocumentReference cirlceDelete =  FirebaseFirestore.getInstance().collection(Constants.CIRCLE_COLLECTION)
                            .document("fd");

                         batch.delete(cirlceDelete);
                         batch.commit().addOnSuccessListener(unused -> {

                         });*/


                    //delete 1st the sub collections and then the main collection

                    // checks if the user has any joined circle and removes that values

                    /*StorageReference storageReference = FirebaseStorage.getInstance().getReference(Constants.PROFILE_IMAGES);

                    storageReference.child("filepath")
                            .delete();*/

                    // dismiss this dialog and then sign out
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.create().show();

    }

    private void deletIdFromCircles(List<CircleModel> filteredCircleList) {
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for(int i=0;  i< filteredCircleList.size(); i++) {
            DocumentReference dr = FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                    .document(filteredCircleList.get(i).getCircleOwnerId())
                    .collection(Constants.CIRCLE_COLLECTION)
                    .document(filteredCircleList.get(i).getCircleId());
            batch.update(dr,Constants.CIRCLE_MEMBERS,FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        }




    }
}