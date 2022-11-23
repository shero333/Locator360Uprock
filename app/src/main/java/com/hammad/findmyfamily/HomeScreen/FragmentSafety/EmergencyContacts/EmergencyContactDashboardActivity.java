package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityEmergencyContactDashboardBinding;

public class EmergencyContactDashboardActivity extends AppCompatActivity {

    private static final String TAG = "EMERG_CON_DASH";

    ActivityEmergencyContactDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initial binding
        binding = ActivityEmergencyContactDashboardBinding.inflate(getLayoutInflater());

        binding.btnAddContact.setOnClickListener(v ->
                Commons.addEmergencyContactDialog(this, isSuccessful -> {

                    if(Permissions.hasContactPermission(this)) {
                        startActivity(new Intent(this, AddContactFromPhoneActivity.class));
                    }
                    else {
                        Permissions.getContactPermission(this);
                    }
                })
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.REQUEST_CODE_CONTACTS) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, AddContactFromPhoneActivity.class));
            }
            else {
                Log.i(TAG, "read contacts permission denied");
                Toast.makeText(this, "Contacts Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}