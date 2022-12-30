package com.hammad.findmyfamily.HomeScreen.FragmentLocation.BottomSheetMembers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityMemberLocationBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemberLocationActivity extends AppCompatActivity {

    ActivityMemberLocationBinding binding;

    // user email
    String email;

    // locations list
    List<LocationInfo> locationInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityMemberLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // getting the intent
        Intent intent = getIntent();
        if(intent != null) {
            email = intent.getStringExtra(Constants.EMAIL);
        }

        binding.calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {

            // sets the selected date time to 12 AM. Like for 30th Dec 2022, 'startTime' will be 30th Dec 2022 12:00 AM 
            Calendar startTime = Calendar.getInstance();
            startTime.set(year, month, day,0,0,0);

            // sets the selected date time to 11:59 PM. Like for 30th Dec 2022, 'endTime' will be 30th Dec 2022 11:59 PM 
            Calendar endTime = Calendar.getInstance();
            endTime.set(year,month,day,23,59,0);

            getLocationsFromFirebase(startTime.getTimeInMillis(),endTime.getTimeInMillis());
        });

        Calendar calendar = Calendar.getInstance();
        
        // current date start time in format like 30th Dec 2022 12:00 AM
        Calendar defaultStartTime = Calendar.getInstance();
        defaultStartTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);

        // current date end time in format like 30th Dec 2022 11:59 PM
        Calendar defaultEndTime = Calendar.getInstance();
        defaultEndTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),23,59,0);
        
        getLocationsFromFirebase(defaultStartTime.getTimeInMillis(),defaultEndTime.getTimeInMillis());
    }

    private void getLocationsFromFirebase(long startTimeInMilliSec, long endTimeInMilliSec) {

        Log.i("HELLO_123", "start time: "+Commons.timeInMilliToDateFormat(String.valueOf(startTimeInMilliSec)));
        Log.i("HELLO_123", "end time: "+Commons.timeInMilliToDateFormat(String.valueOf(endTimeInMilliSec)));

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(email)
                .collection(Constants.LOCATION_COLLECTION)
                .whereGreaterThanOrEqualTo(Constants.LOC_TIMESTAMP,startTimeInMilliSec)
                .whereLessThanOrEqualTo(Constants.LOC_TIMESTAMP,endTimeInMilliSec)
                /*.orderBy(Constants.LOC_TIMESTAMP, Query.Direction.DESCENDING)*/
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.i("HELLO_123", "locations getting success");

                    for(DocumentSnapshot doc: queryDocumentSnapshots) {
                        locationInfoList.add(new LocationInfo(doc.getDouble(Constants.LAT).toString(),
                                doc.getDouble(Constants.LNG).toString(),
                                doc.getString(Constants.LOC_ADDRESS),
                                /*doc.getString(Constants.LOC_TIMESTAMP)*//*Math.toIntExact(*/doc.getLong(Constants.LOC_TIMESTAMP))/*)*/);
                    }

                    if(locationInfoList.size() > 0) {
                        for(int i=0; i < locationInfoList.size(); i++) {
                            Log.i("HELLO_123", "lat: "+locationInfoList.get(i).getLat());
                            Log.i("HELLO_123", "lng: "+locationInfoList.get(i).getLng());
                            Log.i("HELLO_123", "loc address: "+locationInfoList.get(i).getLocAddress());
                            Log.i("HELLO_123", "loc time stamp: "+Commons.timeInMilliToDateFormat(String.valueOf(locationInfoList.get(i).getLocTimestamp())));
                        }
                    }

                })
                .addOnFailureListener(e -> Log.i("HELLO_123", "error getting locations: " + e.getMessage()));

    }

}