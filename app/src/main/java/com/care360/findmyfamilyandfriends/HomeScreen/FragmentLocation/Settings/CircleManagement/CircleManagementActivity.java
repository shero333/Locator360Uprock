package com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.CircleManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.AddMember.AddMemberActivity;
import com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.CircleManagement.RemoveMember.RemoveCircleMemberActivity;
import com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.CircleManagement.ViewMember.ViewCircleMemberActivity;
import com.kl360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.kl360.findmyfamilyandfriends.Util.Constants;
import com.kl360.findmyfamilyandfriends.databinding.ActivityCircleManagementBinding;

public class CircleManagementActivity extends AppCompatActivity {

    private static final String TAG = "CIR_MANAG_ACT";

    ActivityCircleManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityCircleManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarCircleManagement.setNavigationOnClickListener(v -> onBackPressed());

        // add member click listener
        binding.textAddMembers.setOnClickListener(v -> startActivity(new Intent(this, AddMemberActivity.class)));

        // edit text circle name (for circle admin only)
        binding.textEditCircleName.setOnClickListener(v -> startActivity(new Intent(this, EditCircleNameActivity.class)));

        // delete circle (for circle admin only)
        binding.textDeleteCircle.setOnClickListener(v -> deleteCircle());

        // remove circle members (for circle admin only)
        binding.textRemoveMembers.setOnClickListener(v -> startActivity(new Intent(this, RemoveCircleMemberActivity.class)));

        // view circle members (for non-admin members)
        binding.textViewMembers.setOnClickListener(v -> startActivity(new Intent(this, ViewCircleMemberActivity.class)));

        // leave circle (for non-admin members)
        binding.textLeaveCircle.setOnClickListener(v -> leaveCircle());

        // get current Cirlce info
        getCirlceInfo();
    }

    private void getCirlceInfo() {

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(currentUserEmail.equals(SharedPreference.getCircleAdminId())) {
            // current user is admin of this circle

            // setting the visibility of admin views to visible
            binding.groupCircleDetails.setVisibility(View.VISIBLE);
            binding.groupAdmin.setVisibility(View.VISIBLE);

            // setting the visibility of no admin views to gone
            binding.groupNotAdmin.setVisibility(View.GONE);
        }
        else if(!currentUserEmail.equals(SharedPreference.getCircleAdminId())) {
            // current user not admin of this circle

            // setting the visibility of admin views to visible
            binding.groupCircleDetails.setVisibility(View.GONE);
            binding.groupAdmin.setVisibility(View.GONE);

            // setting the visibility of no admin views to gone
            binding.groupNotAdmin.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // if circle name is changed, then the updated name will be displayed
        binding.toolbarCircleManagement.setTitle(SharedPreference.getCircleName());
    }

    // deletes the currently selected circle
    private void deleteCircle() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Circle")
                .setMessage("Want to delete "+SharedPreference.getCircleName()+ " ?")
                .setPositiveButton("Delete", (dialogInterface, i) -> {

                    // progress bar visibility
                    binding.progressBar.setVisibility(View.VISIBLE);

                    // delete the circle
                    FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                            .document(SharedPreference.getCircleAdminId())
                            .collection(Constants.CIRCLE_COLLECTION)
                            .document(SharedPreference.getCircleId())
                            .delete()
                            .addOnSuccessListener(unused ->
                            {
                                // removes the circle id from 'USER' collection circle array as well

                                FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                        .document(SharedPreference.getCircleAdminId())
                                        .update(Constants.CIRCLE_IDS,FieldValue.arrayRemove(SharedPreference.getCircleId()))
                                        .addOnSuccessListener(success ->
                                        {
                                            // progress bar visibility
                                            binding.progressBar.setVisibility(View.GONE);
                                            Log.i(TAG, "circle deleted successfully");

                                            // setting the shared preference values
                                            SharedPreference.setCircleId(Constants.NULL);
                                            SharedPreference.setCircleAdminId(Constants.NULL);
                                            SharedPreference.setCircleName(Constants.NULL);
                                            SharedPreference.setCircleInviteCode(Constants.NULL);

                                            Toast.makeText(this, "Circle deleted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });

                            })
                            .addOnFailureListener(e -> {

                                // progress bar visibility
                                binding.progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "error deleting circle: " + e.getMessage());
                                Toast.makeText(this, "Failed to delete circle", Toast.LENGTH_SHORT).show();
                            });

                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.create();
        builder.show();
    }

    private void leaveCircle() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Leave Circle")
                .setMessage("Want to leave "+SharedPreference.getCircleName()+ " ?")
                .setCancelable(false)
                .setPositiveButton("Leave", (dialogInterface, i) -> {

                    // setting progress view visibility
                    binding.progressBar.setVisibility(View.VISIBLE);

                    String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    // removes the current user from circle
                    FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                            .document(SharedPreference.getCircleAdminId())
                            .collection(Constants.CIRCLE_COLLECTION)
                            .document(SharedPreference.getCircleId())
                            .update(Constants.CIRCLE_MEMBERS, FieldValue.arrayRemove(currentUserEmail))
                            .addOnSuccessListener(unused -> {

                                // setting progress view visibility
                                binding.progressBar.setVisibility(View.GONE);

                                Log.i(TAG, "circle left successfully");

                                // setting the shared preference values
                                SharedPreference.setCircleId(Constants.NULL);
                                SharedPreference.setCircleAdminId(Constants.NULL);
                                SharedPreference.setCircleName(Constants.NULL);
                                SharedPreference.setCircleInviteCode(Constants.NULL);

                                Toast.makeText(this, "Circle left successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            })
                            .addOnFailureListener(e -> {
                                // setting progress view visibility
                                binding.progressBar.setVisibility(View.GONE);

                                Log.i(TAG, "error leaving circle: "+e.getMessage());

                                Toast.makeText(this, "Error! Failed to leave circle.", Toast.LENGTH_SHORT).show();
                            });

                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        alertDialog.create().show();

    }
}