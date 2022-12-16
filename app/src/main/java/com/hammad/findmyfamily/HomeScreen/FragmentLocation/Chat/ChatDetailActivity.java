package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.DB.MessageEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.Message;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.Model.UserInfo;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencySOS.VolleySingleton;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityChatDetailBinding;
import com.hammad.findmyfamily.databinding.LayoutChatImageDialogBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_DETAIL_ACT";

    ActivityChatDetailBinding binding;

    List<Message> messagesList = new ArrayList<>();

    String senderId, receiverId, fcmToken, senderName, imageUrl;

    ChatAdapter chatAdapter;

    //for sending message
    JSONObject jsonNotification = new JSONObject();
    JSONObject jsonData = new JSONObject();
    JSONObject jsonObjectMain = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // sender full name
        Commons.currentUserFullName();

        // get user info intent
        getUserInfoIntent();

        //recyclerview messages
        setRecyclerView();

        // messages list
        getMessagesList();

        // back pressed
        binding.txtBackPressed.setOnClickListener(v -> onBackPressed());

        // image profile click listener (pops up as dialog)
        if (!imageUrl.equals(Constants.NULL)) {
            binding.profileImg.setOnClickListener(v -> profileImageDialog());
        }

        // message send click listener
        binding.imgViewSendMessage.setOnClickListener(v -> {

            String message = binding.editTextMessage.getText().toString();
            String timeStamp = String.valueOf(System.currentTimeMillis());

            if(binding.recyclerViewChat.getVisibility() == View.GONE) {
                binding.recyclerViewChat.setVisibility(View.VISIBLE);
                binding.layoutNoMessages.consNoMessages.setVisibility(View.GONE);
            }

            //send message
            sendMessage(message,timeStamp);
        });

    }

    private void getUserInfoIntent() {

        Intent intent = getIntent();

        if (intent != null) {

            UserInfo userInfo = intent.getParcelableExtra(Constants.KEY_USER_INFO);
            int randColorFromPrevAct = intent.getIntExtra(Constants.RANDOM_COLOR,-1);

            receiverId = userInfo.getUserId();
            fcmToken = userInfo.getUserToken();

            // get the sender info
            senderId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            senderName = SharedPreference.getFullName();

            imageUrl = userInfo.getUserImageURL();

            //setting the values to views
            binding.txtUserName.setText(userInfo.getUserFullName());

            if (imageUrl.equals(Constants.NULL)) {
                binding.profileImg.setBackgroundColor(randColorFromPrevAct);
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

        //get the messages from SQLite (if any)

        if (messagesList.size() > 0) {
            //no messages layout visibility to gone
            binding.layoutNoMessages.consNoMessages.setVisibility(View.GONE);
            binding.recyclerViewChat.setVisibility(View.VISIBLE);
        }
        else if (messagesList.size() == 0) {
            //no messages layout visibility to VISIBLE
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

    private void sendMessage(String message, String timeStamp) {

        // setting the json
        try {

            //setting the send message progress bar to VISIBLE
            binding.progressSendMessage.setVisibility(View.VISIBLE);

            jsonObjectMain.put(Constants.FCM_TO,fcmToken);

            jsonNotification.put(Constants.FCM_TITLE,SharedPreference.getFullName());
            jsonNotification.put(Constants.FCM_BODY,message);


            jsonObjectMain.put(Constants.FCM_NOTIFICATION,jsonNotification);

            jsonData.put(Constants.SENDER_ID,senderId);
            jsonData.put(Constants.RECEIVER_ID,receiverId);
            jsonData.put(Constants.TIMESTAMP,timeStamp);

            jsonObjectMain.put(Constants.FCM_DATA,jsonData);

            //object request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constants.FCM_BASE_URL, jsonObjectMain,
                    response -> {
                        Log.i(TAG, "json send message request successful");

                        //setting the send message progress bar to GONE
                        binding.progressSendMessage.setVisibility(View.GONE);

                        // add the new message in message list
                        messagesList.add(new Message(SharedPreference.getFullName(),senderId,receiverId,message,timeStamp));

                        //clears the edit text
                        binding.editTextMessage.getText().clear();

                        //more recyclerview to the newly added item position
                        changeAdapterPosition();

                        //saves the message in db
                        //saveMessageToDatabase(message,timeStamp);
                    },
                    error -> {
                        Log.e(TAG, "json message send error: "+error.getMessage());

                        //setting the send message progress bar to GONE
                        binding.progressSendMessage.setVisibility(View.GONE);

                        Toast.makeText(this, "Error! Failed to send message.", Toast.LENGTH_LONG).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put(Constants.FCM_HEADER_AUTH,"key="+Constants.FCM_SERVER_KEY);
                    params.put(Constants.CONTENT_TYPE_KEY,Constants.CONTENT_TYPE);
                    return params;
                }
            };

            // volley instance
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        }
        catch (JSONException e) {
            e.printStackTrace();

            //setting the send message progress bar to GONE
            binding.progressSendMessage.setVisibility(View.GONE);

            Toast.makeText(this, "Failed to send message.", Toast.LENGTH_LONG).show();
        }

    }

    private void saveMessageToDatabase(String message, String timeStamp) {

        RoomDBHelper.getInstance(this)
                .messageDao()
                .saveMessage(new MessageEntity(senderId,senderId,receiverId,message,timeStamp));
    }
}