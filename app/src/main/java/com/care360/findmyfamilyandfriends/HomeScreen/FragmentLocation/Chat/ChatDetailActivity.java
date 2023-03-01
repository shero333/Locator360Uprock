package com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat;

import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.DB.MessageEntity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.Model.UserInfo;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencySOS.VolleySingleton;
import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.StartScreen.StartScreenActivity;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityChatDetailBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutChatImageDialogBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_DETAIL_ACT";

    ActivityChatDetailBinding binding;

    String senderId, fcmToken, senderName, imageUrl;

    static String receiverId;

    ChatAdapter chatAdapter;

    //for sending message
    JSONObject jsonNotification = new JSONObject();
    JSONObject jsonData = new JSONObject();
    JSONObject jsonObjectMain = new JSONObject();

    static boolean isActivityActive = true;

    // boolean for checking whether this activity was opened when app is in foreground or background
    private boolean isAppInForeground;

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

        // messages list
        getMessagesList();

        // tool back pressed
        if(isAppInForeground) {
            binding.txtBackPressed.setOnClickListener(v -> checkCurrentLoginStatus());
        }
        else if(!isAppInForeground) {
            binding.txtBackPressed.setOnClickListener(v -> navigateToStartScreen());
        }

        // image profile click listener (pops up as dialog)
        if (!imageUrl.equals(Constants.NULL)) {
            binding.profileImg.setOnClickListener(v -> profileImageDialog());
        }

        // message send click listener
        binding.imgViewSendMessage.setOnClickListener(v -> {

            String message = binding.editTextMessage.getText().toString();

            if(binding.recyclerViewChat.getVisibility() == View.GONE) {
                binding.recyclerViewChat.setVisibility(View.VISIBLE);
                binding.layoutNoMessages.consNoMessages.setVisibility(View.GONE);
            }

            //send message
            sendMessage(message);
        });

    }

    private void getUserInfoIntent() {

        Intent intent = getIntent();

        if (intent != null) {

            UserInfo userInfo = intent.getParcelableExtra(Constants.KEY_USER_INFO);
            int randColorFromPrevAct = intent.getIntExtra(Constants.RANDOM_COLOR,-1);

            isAppInForeground = intent.getBooleanExtra(Constants.IS_APP_IN_FOREGROUND,true);

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
            }
            else if (!imageUrl.equals(Constants.NULL)) {
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
        RoomDBHelper.getInstance(this)
                .messageDao().getMessagesList(senderId,receiverId).observe(this,list -> {

                    if (list.size() > 0) {
                        //no messages layout visibility to gone
                        binding.layoutNoMessages.consNoMessages.setVisibility(View.GONE);
                        binding.recyclerViewChat.setVisibility(View.VISIBLE);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true/*false*/);
                        binding.recyclerViewChat.setLayoutManager(layoutManager);

                        chatAdapter = new ChatAdapter(this, list);
                        binding.recyclerViewChat.setAdapter(chatAdapter);

                        //scrolls to new position
                        changeAdapterPosition(list);

                    }
                    else if (list.size() == 0) {
                        //no messages layout visibility to VISIBLE
                        binding.recyclerViewChat.setVisibility(View.GONE);
                        binding.layoutNoMessages.consNoMessages.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void changeAdapterPosition(List<MessageEntity> list) {

        int newPosition = list.size() - 1;
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

    private void sendMessage(String message) {

        // setting the json
        try {

            String timeStamp = String.valueOf(System.currentTimeMillis());

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

                        //clears the edit text
                        binding.editTextMessage.getText().clear();

                        //saves the message in db
                        saveMessageToDatabase(message,timeStamp);
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
                .saveMessage(new MessageEntity(senderId,receiverId,message,timeStamp));
    }

    public static boolean isOnline(String senderId) {
        return isActivityActive && senderId.equals(receiverId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityActive = false;
    }

    @Override
    public void onBackPressed() {
        if(isAppInForeground) {
            checkCurrentLoginStatus();
        }
        else if(!isAppInForeground) {
            navigateToStartScreen();
        }

    }

    private void navigateToStartScreen() {
        startActivity(new Intent(this, StartScreenActivity.class));
        finish();
    }

    private void checkCurrentLoginStatus() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection(USERS_COLLECTION).document(email);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {

                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        }
        else if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this,StartScreenActivity.class));
            finish();
        }

    }
}