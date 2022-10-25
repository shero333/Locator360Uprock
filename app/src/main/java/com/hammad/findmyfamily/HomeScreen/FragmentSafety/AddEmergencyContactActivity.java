package com.hammad.findmyfamily.HomeScreen.FragmentSafety;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hammad.findmyfamily.databinding.ActivityAddEmergencyContactBinding;
import com.hammad.findmyfamily.databinding.LayoutDialogAddEmergncyContactBinding;

public class AddEmergencyContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        ActivityAddEmergencyContactBinding binding = ActivityAddEmergencyContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnInviteContact.setOnClickListener(v -> addEmergencyContacts());
    }

    private void addEmergencyContacts() {

        Dialog addContactsDialog = new Dialog(this);

        LayoutDialogAddEmergncyContactBinding dialogBinding = LayoutDialogAddEmergncyContactBinding.inflate(LayoutInflater.from(this));
        addContactsDialog.setContentView(dialogBinding.getRoot());

        //dialog add from contact button click listener
        dialogBinding.btnSelectFromContact.setOnClickListener(view -> {
            Toast.makeText(this, "Add from Contact", Toast.LENGTH_SHORT).show();
            addContactsDialog.dismiss();
        });

        //add contact manually
        dialogBinding.txtAddManually.setOnClickListener(view -> {
            Toast.makeText(this, "Add Manually", Toast.LENGTH_SHORT).show();
            addContactsDialog.dismiss();
        });

        //setting the transparent background
        addContactsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        addContactsDialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        addContactsDialog.show();
    }
}