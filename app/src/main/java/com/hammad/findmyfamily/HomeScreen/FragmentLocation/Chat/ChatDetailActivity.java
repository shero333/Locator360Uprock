package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityChatDetailBinding;
import com.hammad.findmyfamily.databinding.LayoutChatImageDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    List<String> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // messages list
        getMessagesList();

        // back pressed
        binding.txtBackPressed.setOnClickListener(v -> onBackPressed());

        //image profile click listener (pops up as dialog)
        binding.profileImg.setOnClickListener(v -> profileImageDialog());

        //on action send button, message is send
        binding.editTextMessage.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            if(actionId == EditorInfo.IME_ACTION_SEND) {
                Toast.makeText(this, binding.editTextMessage.getText().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

    }

    private void getMessagesList() {

        if(messagesList.size() > 0) {

            //recyclerview messages
            setRecyclerView();

            //no messages layout visibility to gone
            binding.noMessage.consNoMessages.setVisibility(View.GONE);
            binding.recyclerViewChat.setVisibility(View.VISIBLE);
        }
        else if(messagesList.size() == 0) {

            binding.recyclerViewChat.setVisibility(View.GONE);
            binding.noMessage.consNoMessages.setVisibility(View.VISIBLE);
        }
    }

    private void profileImageDialog() {

        Dialog dialog = new Dialog(this);

        LayoutChatImageDialogBinding binding = LayoutChatImageDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        //loading image in Glide
        Glide
             .with(this)
             .load(R.drawable.background_start_screen)
             .into(binding.imagePreview);


        //setting the animation
        dialog.getWindow().getAttributes().windowAnimations=R.style.SlidingDialogAnimation;

        //this sets the width & height of dialog to 70%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.7);
        int height = (int) (displayMetrics.heightPixels * 0.7);

        //setting the width and height of alert dialog
        dialog.getWindow().setLayout(width, height);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recyclerViewChat.setLayoutManager(layoutManager);

        ChatMessageAdapter adapter = new ChatMessageAdapter(this,messagesList);
        binding.recyclerViewChat.setAdapter(adapter);
    }
}