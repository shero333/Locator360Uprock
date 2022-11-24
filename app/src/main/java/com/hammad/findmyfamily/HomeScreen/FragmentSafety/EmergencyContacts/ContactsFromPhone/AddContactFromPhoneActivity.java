package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityAddContactFromPhoneBinding;

import java.util.ArrayList;
import java.util.List;

public class AddContactFromPhoneActivity extends AppCompatActivity implements ContactAdapter.OnAddContactListener {

    ActivityAddContactFromPhoneBinding binding;

    List<ContactModel> phoneContactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddContactFromPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setRecyclerview() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recyclerAddContact.setLayoutManager(layoutManager);

        ContactAdapter contactAdapter = new ContactAdapter(this,phoneContactList,this);
        binding.recyclerAddContact.setAdapter(contactAdapter);

    }

    // Contact Adapter click listener
    @Override
    public void onAddContactClick(int position) {
        Toast.makeText(this, "Item: "+position, Toast.LENGTH_SHORT).show();
    }
}