package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone.AddContactFromPhoneActivity;
import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityEmergencyContactDashboardBinding;

import java.util.List;

public class EmergencyContactDashboardActivity extends AppCompatActivity {

    private static final String TAG = "EMERG_CON_DASH";

    ActivityEmergencyContactDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initial binding
        binding = ActivityEmergencyContactDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //when a new emergency contact is added, the invitation sms will be triggered from this function
        getIntentData();

        // get added emergency contacts list
        getEmergencyContactListFromDB();

        binding.btnAddContact.setOnClickListener(v -> Commons.addEmergencyContactDialog(this, isSuccessful -> {

            if (Permissions.hasContactPermission(this)) {
                navigateToNextActivity();
            } else {
                Permissions.getContactPermission(this);
            }
        }));

    }

    private void getIntentData() {

        Intent intent = getIntent();

        if (intent != null) {
            String phoneNo = intent.getStringExtra(Constants.PHONE_NO);

            if (phoneNo != null) {
                Commons.sendEmergencyContactInvitation(this, phoneNo);
            }
        }
    }

    private void getEmergencyContactListFromDB() {

        List<EmergencyContactEntity> emergencyContactList = RoomDBHelper.getInstance(this)
                .emergencyContactDao()
                .getEmergencyContactsList(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // setting the list to recyclerview

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
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}