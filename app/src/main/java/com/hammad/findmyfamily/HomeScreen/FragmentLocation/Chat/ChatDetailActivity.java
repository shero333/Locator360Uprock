package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.Message;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.UserInfo;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityChatDetailBinding;
import com.hammad.findmyfamily.databinding.LayoutChatImageDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;

    List<Message> messagesList = new ArrayList<>();

    String senderId, receiverId, fcmToken, senderName, imageUrl;

    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // send full name
        Commons.currentUserFullName();

        // get user info intent
        getUserInfoIntent();

        //recyclerview messages
        setRecyclerView();

        // messages list
        //getMessagesList();

        // back pressed
        binding.txtBackPressed.setOnClickListener(v -> onBackPressed());

        // image profile click listener (pops up as dialog)
        if (!imageUrl.equals(Constants.NULL)) {
            binding.profileImg.setOnClickListener(v -> profileImageDialog());
        }

        // message send click listener
        binding.imgViewSendMessage.setOnClickListener(v -> {

            messagesList.add(new Message(senderId,receiverId,binding.editTextMessage.getText().toString(),String.valueOf(System.currentTimeMillis())));

            //clears the edit text
            binding.editTextMessage.setText("");

            //more recyclerview to the newly added item position
            changeAdapterPosition();
        });

        // contact name click listener
        binding.txtUserName.setOnClickListener(v -> {

            messagesList.add(new Message(receiverId,senderId,binding.editTextMessage.getText().toString(),String.valueOf(System.currentTimeMillis())));

            //clears the edit text
            binding.editTextMessage.setText("");

            //more recyclerview to the newly added item position
            changeAdapterPosition();
        });

    }

    private void getUserInfoIntent() {

        Intent intent = getIntent();

        if (intent != null) {

            UserInfo userInfo = intent.getParcelableExtra(Constants.KEY_USER_INFO);

            receiverId = userInfo.getUserId();
            fcmToken = userInfo.getUserToken();

            // get the sender info
            senderId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            senderName = SharedPreference.getFullName();

            imageUrl = userInfo.getUserImageURL();

            //setting the values to views
            binding.txtUserName.setText(userInfo.getUserFullName());

            if (imageUrl.equals(Constants.NULL)) {
                binding.profileImg.setBackgroundColor(Commons.randomColor());
                binding.txtNameFirstChar.setText(String.valueOf(userInfo.getUserFullName().charAt(0)));
                binding.txtNameFirstChar.setVisibility(View.VISIBLE);
            } else if (!imageUrl.equals(Constants.NULL)) {
                binding.txtNameFirstChar.setVisibility(View.GONE);

                //loading the image
                Glide
                        .with(this)
                        .load(imageUrl)
                        .into(binding.profileImg);
            }
        }

    }

    private void getMessagesList() {

        if (messagesList.size() > 0) {

            //recyclerview messages
            setRecyclerView();

            //no messages layout visibility to gone
            binding.layoutNoMessages.consNoMessages.setVisibility(View.GONE);
            binding.recyclerViewChat.setVisibility(View.VISIBLE);
        }
        else if (messagesList.size() == 0) {

            binding.recyclerViewChat.setVisibility(View.GONE);
            binding.layoutNoMessages.consNoMessages.setVisibility(View.VISIBLE);
        }
    }

    private void changeAdapterPosition() {

        int newPosition = messagesList.size() - 1;
        chatAdapter.notifyItemInserted(newPosition);
        binding.recyclerViewChat.scrollToPosition(newPosition);
    }

    private void profileImageDialog() {

        Dialog dialog = new Dialog(this);

        LayoutChatImageDialogBinding binding = LayoutChatImageDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        //loading image in Glide
        Glide
                .with(this)
                .load(imageUrl)
                .into(binding.imagePreview);


        //setting the animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerViewChat.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(this, messagesList);
        binding.recyclerViewChat.setAdapter(chatAdapter);
    }
}