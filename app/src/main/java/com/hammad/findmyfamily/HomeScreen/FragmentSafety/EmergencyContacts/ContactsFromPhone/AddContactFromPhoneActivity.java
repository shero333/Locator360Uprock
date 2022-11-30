package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.EmergencyContactEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard.EmergencyContactDashboardActivity;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityAddContactFromPhoneBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddContactFromPhoneActivity extends AppCompatActivity implements ContactAdapter.OnAddContactListener {

    ActivityAddContactFromPhoneBinding binding;

    List<ContactModel> phoneContactList = new ArrayList<>();

    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddContactFromPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // setting the action bar
        setSupportActionBar(binding.toolbarAddContact);

        //toolbar back button
        binding.toolbarAddContact.setNavigationOnClickListener(v -> onBackPressed());

        //getting contacts from phone
        getPhoneContactsList();
    }

    private void getPhoneContactsList() {

        String id,rawId,contactName,contactNumber;

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(uri,null,null,null,sortOrder);

        // set the missing contacts issue
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                rawId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));

                if(id.equals(rawId))
                {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    phoneContactList.add(new ContactModel(id,contactName,contactNumber));
                }
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
}