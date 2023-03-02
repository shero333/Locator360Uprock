package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone.AddContactFromPhoneActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityEmergencyContactDashboardBinding;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactDashboardActivity extends AppCompatActivity implements DashboardContactsAdapter.OnAddedContactListener {

    private static final String TAG = "EMERG_CON_DASH";

    ActivityEmergencyContactDashboardBinding binding;

    List<EmergencyContactEntity> emergencyContactList = new ArrayList<>();

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initial binding
        binding = ActivityEmergencyContactDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();


        //when a new emergency contact is added, the invitation sms will be triggered from this function
        getIntentData();

        // add new contact click listener
        binding.btnAddNewContact.setOnClickListener(v -> Commons.addEmergencyContactDialog(this, isSuccessful -> {

            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    setAd();

                    if (Permissions.hasContactPermission(getApplicationContext())) {
                        navigateToNextActivity();
                    } else {
                        Permissions.getContactPermission(EmergencyContactDashboardActivity.this);
                    }
                }
            });

        }));

        //back pressed
        binding.toolbarContactDashboard.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get added emergency contacts list
        getEmergencyContactListFromDB();
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

         emergencyContactList = RoomDBHelper.getInstance(this).emergencyContactDao()
                                .getEmergencyContactsList(FirebaseAuth.getInstance().getCurrentUser().getEmail());

         //if added contacts list is zero, then added contacts status is set to false.
         if(emergencyContactList.size() == 0) {
             SharedPreference.setEmergencyContactsStatus(false);

             //finish this activity
             finish();
         }

        // setting the list to recyclerview
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);

        binding.recyclerAddedContact.setLayoutManager(layoutManager);

        DashboardContactsAdapter adapter = new DashboardContactsAdapter(this,emergencyContactList,this);
        binding.recyclerAddedContact.setAdapter(adapter);
    }

    // recyclerview item click
    @Override
    public void onAddedContact(int position) {

        EmergencyContactEntity contactItem = emergencyContactList.get(position);

        Intent intent = new Intent(this,ContactDetailActivity.class);
        intent.putExtra(Constants.CONTACT_KEY,new ParcelableContactModel(contactItem.getOwnerEmail(),contactItem.getContactId(),contactItem.getContactName(),contactItem.getContactNo()));
        startActivity(intent);
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

    private void setAd() {

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {

                        Log.d("AdError", adError.toString());
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d("AdError", "Ad was loaded.");
                        mInterstitialAd = interstitialAd;
                    }
                });
    }

}