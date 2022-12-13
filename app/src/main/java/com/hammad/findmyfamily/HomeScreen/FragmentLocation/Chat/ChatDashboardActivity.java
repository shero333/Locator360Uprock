package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.databinding.ActivityChatDashboardBinding;

import java.util.List;

public class ChatDashboardActivity extends AppCompatActivity implements ChatDashboardAdapter.OnChatMemberListener {

    ActivityChatDashboardBinding binding;

    //members list
    List<String> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityChatDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // edit text search
        binding.editTextSearch.addTextChangedListener(searchMemberTextWatcher);

        //recycler view
        setRecyclerView();
    }

    private final TextWatcher searchMemberTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void setRecyclerView() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recyclerView.setLayoutManager(layoutManager);

        ChatDashboardAdapter adapter = new ChatDashboardAdapter(this,memberList,this);
        binding.recyclerView.setAdapter(adapter);
    }

    //member click listener
    @Override
    public void onChatMemberClick(int position) {
        Toast.makeText(this, "Position: "+position, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,ChatDetailActivity.class));
    }
}