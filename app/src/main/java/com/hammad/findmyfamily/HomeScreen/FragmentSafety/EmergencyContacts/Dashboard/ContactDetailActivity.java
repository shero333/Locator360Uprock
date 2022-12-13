package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityContactDetailBinding;

public class ContactDetailActivity extends AppCompatActivity {

    ActivityContactDetailBinding binding;

    ParcelableContactModel contactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityContactDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //getting the intent data
        Intent intent = getIntent();

        if(intent != null) {
            contactModel = intent.getParcelableExtra(Constants.CONTACT_KEY);
        }

        //setting the values to views
        binding.txtContactLetters.setText(Commons.getContactLetters(contactModel.getParContactName()));
        binding.txtContactName.setText(contactModel.getParContactName());
        binding.txtContactNumber.setText(contactModel.getParContactNumber());

        //resend invite click listener
        binding.btnResentInvite.setOnClickListener(v -> {

            if(SharedPreference.getFullName().equals(Constants.NULL)) {
                Commons.currentUserFullName();
            }

            //sending
            Commons.sendEmergencyContactInvitation(this, contactModel.getParContactNumber());
        });

        // button delete
        binding.deleteContact.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Delete Contact")
                    .setMessage("want to delete this emergency contact?")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {

                        //deleting contact
                        RoomDBHelper.getInstance(this).emergencyContactDao()
                                .deleteEmergencyContact(contactModel.getParOwnerEmail(), contactModel.getParContactId(), contactModel.getParContactNumber());

                        finish();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();

        });
    }
}