package com.hammad.findmyfamily.HomeScreen.FragmentSafety;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.EmergencyContactEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityEmergencySosBinding;

import java.util.List;

public class EmergencySOSActivity extends AppCompatActivity implements LocationListener {

    ActivityEmergencySosBinding binding;
    CountDownTimer countDownTimer;

    boolean isSosTriggerClicked = false;

    List<EmergencyContactEntity> emergencyContactList;

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
        if(!isSosTriggerClicked)
        {
            //setting the 'isSosTriggerClicked' to true
            isSosTriggerClicked = true;

            countDownTimer = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long l) {

                    if(l == 10000) {
                        binding.txtTimer.setText(getString(R.string.time_start_with_zero).concat(" ".concat(String.valueOf(l/1000))));
                    }
                    else {

                        //setting the count down
                        binding.txtTimer.setText(getString(R.string.time_start).concat(String.valueOf(l/1000)));

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

        }
        else if(isSosTriggerClicked)
        {
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
        if(SharedPreference.getFullName().equals(Constants.NULL)) {
            Commons.currentUserFullName();
        }

        //send sms to emergency contacts through
        //sendSOSTextMessage();

        //send firebase notification to circle members
        sendSOSThroughFirebase();

        //finish the current activity
        //finish();
    }

    private void sendSOSTextMessage() {

        Location location = getCurrentLocation();

        String messageBody = "I ".concat(SharedPreference.getFullName()
                .concat(" need ur help. Please reached out to me Urgently.\nMy location coordinates are:\n")
                .concat("http://maps.google.com/?q="
                        + location.getLatitude() + ","
                        + location.getLongitude()));

        SmsManager smsManager = SmsManager.getDefault();

        //send message to all emergency contacts
        for (EmergencyContactEntity emergContact: emergencyContactList) {
            smsManager.sendTextMessage(emergContact.getContactNo(),null,messageBody,null,null);
        }

    }

    private void sendSOSThroughFirebase() {

        //gets the current circle id
        List<String> currentCircleMembersList;

        //gets the current cirlce members and their fcm tokens
    }

    @SuppressLint("MissingPermission")
    private Location getCurrentLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // returns current location
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}