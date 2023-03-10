package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.ContactsFromPhone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.databinding.ActivityAddContactFromPhoneBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.Dashboard.EmergencyContactDashboardActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddContactFromPhoneActivity extends AppCompatActivity implements ContactAdapter.OnAddContactListener {

    ActivityAddContactFromPhoneBinding binding;

    List<ContactModel> phoneContactList = new ArrayList<>();

    ContactAdapter contactAdapter;

    ContactModel contactModel;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddContactFromPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        // setting the action bar
        setSupportActionBar(binding.toolbarAddContact);

        //toolbar back button
        binding.toolbarAddContact.setNavigationOnClickListener(v -> onBackPressed());

        //getting contacts from phone
        getPhoneContactsList();
    }

    private void getPhoneContactsList() {

        String contactId,contactName,contactNumber;

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(uri,null,null,null,sortOrder);

        // set the missing contacts issue
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                contactModel = new ContactModel();

                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactModel.setContactId(contactId);
                contactModel.setContactName(contactName);
                contactModel.setContactNumber(contactNumber);

                //remove duplicates
                removeDuplicateContacts();
            }
        }

        cursor.close();

        if(phoneContactList.size() > 0) {

            //setting the recyclerview
            setRecyclerview();

            //setting the no contacts found view visibility to gone
            binding.txtNoContacts.setVisibility(View.GONE);
        }
        else if(phoneContactList.size() == 0) {
            binding.txtNoContacts.setVisibility(View.VISIBLE);
            binding.recyclerAddContact.setVisibility(View.GONE);
        }

    }

    private void removeDuplicateContacts() {
        int flag = 0;

        if (phoneContactList.size() == 0) {
            phoneContactList.add(contactModel);
        }

        for (int i = 0; i < phoneContactList.size(); i++) {

            if (phoneContactList.get(i).getContactId().equals(contactModel.getContactId())) {
                flag = 0;
                break;
            } else {
                flag = 1;
            }
        }

        if (flag == 1) {
            phoneContactList.add(contactModel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflating menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_contact,menu);

        //menu item
        MenuItem menuItem = menu.findItem(R.id.search_view);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();

        //setting the options to done on soft keyboard so that it can disappears
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setRecyclerview() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recyclerAddContact.setLayoutManager(layoutManager);

        contactAdapter = new ContactAdapter(this,phoneContactList,this);
        binding.recyclerAddContact.setAdapter(contactAdapter);

    }

    // Contact Adapter click listener
    @Override
    public void onAddContactClick(int position) {

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        ContactModel contactItem = phoneContactList.get(position);

        //saving data in database
        RoomDBHelper.getInstance(this)
                    .emergencyContactDao()
                    .addContact(new EmergencyContactEntity(currentUserEmail, contactItem.getContactId(),contactItem.getContactName(),contactItem.getContactNumber()));

        //updating status in shared preference
        SharedPreference.setEmergencyContactsStatus(true);

        // is full name shared pref is null, gets the current user full name from firebase
        if(SharedPreference.getFullName().equals(Constants.NULL)) {
            Commons.currentUserFullName();
        }

        //navigates to next activity
        Intent intent = new Intent(this, EmergencyContactDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Constants.PHONE_NO,contactItem.getContactNumber());
        startActivity(intent);

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