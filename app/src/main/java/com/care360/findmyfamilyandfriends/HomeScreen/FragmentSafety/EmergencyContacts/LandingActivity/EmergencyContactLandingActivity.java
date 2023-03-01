package com.kl360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.LandingActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kl360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone.AddContactFromPhoneActivity;
import com.kl360.findmyfamilyandfriends.Permission.Permissions;
import com.kl360.findmyfamilyandfriends.Util.Commons;
import com.kl360.findmyfamilyandfriends.Util.Constants;
import com.kl360.findmyfamilyandfriends.databinding.ActivityEmergencyContactLandingBinding;

public class EmergencyContactLandingActivity extends AppCompatActivity {

    private static final String TAG = "EMERG_CONTACT_LAND_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        ActivityEmergencyContactLandingBinding binding = ActivityEmergencyContactLandingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnInviteContact.setOnClickListener(v -> Commons.addEmergencyContactDialog(this, isSuccessful -> {

            if (Permissions.hasContactPermission(this)) {
                navigateToNextActivity();
            } else {
                Permissions.getContactPermission(this);
            }
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToNextActivity();
            } else {
                Log.i(TAG, "read contacts permission denied");
                Toast.makeText(this, "Contacts Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(this, AddContactFromPhoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

}