package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityAddContactBinding;

public class AddContactActivity extends AppCompatActivity implements ContactAdapter.OnAddContactListener {

    ActivityAddContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //toolbar back button
        binding.toolbarAddContact.setNavigationOnClickListener(view1 -> onBackPressed());

        // recyclerview add contacts
        setRecyclerview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflating menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_contact,menu);

        //menu item
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setQueryHint("Choose Contact");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setRecyclerview() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recyclerAddContact.setLayoutManager(layoutManager);

        ContactAdapter contactAdapter = new ContactAdapter(this,this);
        binding.recyclerAddContact.setAdapter(contactAdapter);

    }

    // Contact Adapter click listener
    @Override
    public void onAddContactClick(int position) {
        Toast.makeText(this, "Item: "+position, Toast.LENGTH_SHORT).show();
    }
}