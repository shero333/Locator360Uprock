package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencySOS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityEmergencySosBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmergencySOSActivity extends AppCompatActivity {

    private static final String TAG = "EMERG_SOS_ACT";

    ActivityEmergencySosBinding binding;
    CountDownTimer countDownTimer;

    boolean isSosTriggerClicked = false;

    List<EmergencyContactEntity> emergencyContactList;

    // circle members fcm token list
    List<String> fcmTokenList = new ArrayList<>();

    JSONObject jsonNotification = new JSONObject();
    JSONObject jsonData = new JSONObject();
    JSONObject jsonObjectMain = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityEmergencySosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //emergency contacts list
        emergencyContactList = RoomDBHelper.getInstance(this)
                .emergencyContactDao()
                .getEmergencyContactsList(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // triggering SOS
        triggerSOS();

        binding.txtSosTrigger.setOnClickListener(v -> triggerSOS());

    }

    private void triggerSOS() {

        // starts the count-down
        if (!isSosTriggerClicked) {

            //setting the 'isSosTriggerClicked' to true
            isSosTriggerClicked = true;

            countDownTimer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long l) {

                    if (l == 10000) {
                        binding.txtTimer.setText(getString(R.string.time_start_with_zero).concat(" ".concat(String.valueOf(l / 1000))));
                    } else {

                        //setting the count down
                        binding.txtTimer.setText(getString(R.string.time_start).concat(String.valueOf(l / 1000)));

                        //setting txt helper stop timer visibility to VISIBLE
                        binding.txtHelperStopTimer.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFinish() {

                    //setting the default text to timer text
                    binding.txtTimer.setText(getString(R.string.timer_start));

                    //setting txt helper stop timer visibility to VISIBLE
                    binding.txtHelperStopTimer.setVisibility(View.GONE);

                    //triggers the emergency sos
                    sendEmergencyDetails();
                }
            }.start();

        } else if (isSosTriggerClicked) {
            //setting the 'isSosTriggerClicked' to false
            isSosTriggerClicked = false;

            //stops the count down
            countDownTimer.cancel();

            //setting the default text to timer text
            binding.txtTimer.setText(getString(R.string.timer_start));

            //setting txt helper stop timer visibility to VISIBLE
            binding.txtHelperStopTimer.setVisibility(View.GONE);
        }

    }

    private void sendEmergencyDetails() {

        // is full name shared pref is null, gets the current user full name from firebase
        if (SharedPreference.getFullName().equals(Constants.NULL)) {
            Commons.currentUserFullName();
        }

        //send sms to emergency contacts through
        sendSOSTextMessage();

        //send firebase notification to circle members
        sendSOSThroughFirebase();

        //finish the current activity
        finish();
    }

    private void sendSOSTextMessage() {

        Location location = getCurrentLocation();

        String messageBody = "I ".concat(SharedPreference.getFullName()
                .concat(" need ur help. Please reached out to me Urgently.\nMy location coordinates are:\n")
                .concat("http://maps.google.com/?q="
                        + location.getLatitude() + ","
                        + location.getLongitude()));

        SmsManager smsManager = SmsManager.getDefault();

        if(emergencyContactList.size() > 0) {

            //send message to all emergency contacts
            for (EmergencyContactEntity emergContact : emergencyContactList) {
                smsManager.sendTextMessage(emergContact.getContactNo(), null, messageBody, null, null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void sendSOSThroughFirebase() {

        //list of circle members
        List<String> circleMemberList = new ArrayList<>();

        //current user email
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //gets the current cirlce members and their fcm tokens
        FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                .whereEqualTo(Constants.CIRCLE_ID, SharedPreference.getCircleId())
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (DocumentSnapshot doc : querySnapshot) {
                        ArrayList<String> list = (ArrayList<String>) doc.get(Constants.CIRCLE_MEMBERS);
                        circleMemberList.addAll(list);
                    }

                    // get the fcm tokens of circle members
                    for(int i = 0; i < circleMemberList.size(); i++) {

                        /*
                            this variable temporarily saves the current for loop index number.
                            if the value is equal to last index, then fcm cloud message function is called
                        */
                        int loopCurrentIndexValue = i;

                        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                .document(circleMemberList.get(i))
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if(!circleMemberList.get(loopCurrentIndexValue).equals(currentUserEmail)) {
                                        String fcmToken = documentSnapshot.getString(Constants.FCM_TOKEN);

                                        if (!fcmToken.equals(Constants.NULL)) {
                                            fcmTokenList.add(fcmToken);
                                        }
                                    }

                                    // if current loop iteration is last, then the fcm cloud message notification function is called
                                    if(loopCurrentIndexValue == (circleMemberList.size() - 1)) {
                                        triggerFirebaseCloudMessage();
                                    }

                                });
                    }

                })
                .addOnFailureListener(e -> Log.i(TAG, "failed to get circle members: " + e.getMessage()));
    }

    private void triggerFirebaseCloudMessage() {

        Location location = getCurrentLocation();

        for(String token: fcmTokenList) {
            try {

                String currentUserFullName = SharedPreference.getFullName();

                String notificationBody = currentUserFullName.concat(" ").concat(getString(R.string.sos_body));

                // setting the json
                jsonObjectMain.put(Constants.FCM_TO,token);

                jsonNotification.put(Constants.FCM_TITLE,getString(R.string.sos_title));
                jsonNotification.put(Constants.FCM_BODY,notificationBody);

                jsonObjectMain.put(Constants.FCM_NOTIFICATION, jsonNotification);

                jsonData.put(Constants.FCM_LAT,location.getLatitude());
                jsonData.put(Constants.FCM_LNG,location.getLongitude());

                jsonObjectMain.put(Constants.FCM_DATA,jsonData);

               //object request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constants.FCM_BASE_URL, jsonObjectMain,
                        response -> Log.i(TAG, "json request successful"),
                        error -> Log.e(TAG, "json error: "+error.getMessage())) {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("MissingPermission")
    private Location getCurrentLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // returns current location
    }
}