package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.BottomSheetMembers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.care360.findmyfamilyandfriends.databinding.ActivityMemberLocationBinding;
import com.care360.findmyfamilyandfriends.databinding.CustomMarkerBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutCalendarViewBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemberLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MEM_LOC_ACT";

    ActivityMemberLocationBinding binding;

    // user email
    String email,userFirstName;

    // locations list
    List<LocationInfo> locationInfoList = new ArrayList<>();

    private GoogleMap mGoogleMap;

    // calendar view dialog
    Dialog calendarViewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityMemberLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // toolbar back pressed
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // get data from intent
        getIntentData();

        // initialize map
        initializeMap();

        // get the current date locations (if any)
        getCurrentDateLocation();

        // button calendar view dialog
        binding.btnShowCalendarDialog.setOnClickListener(v -> showCalendarDialog());

    }

    private void getIntentData() {

        Intent intent = getIntent();

        if(intent != null) {
            email = intent.getStringExtra(Constants.EMAIL);
            userFirstName = intent.getStringExtra(Constants.FIRST_NAME);

            // getting the user full name from intent and set it to toolbar title
            binding.toolbar.setTitle(userFirstName.trim().concat("'s Locations"));
        }
    }

    private void initializeMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(binding.mapDetailLocations.getId());

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void getCurrentDateLocation() {

        Calendar calendar = Calendar.getInstance();

        // current date start time in format like 30th Dec 2022 12:00 AM
        Calendar defaultStartTime = Calendar.getInstance();
        defaultStartTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);

        // current date end time in format like 30th Dec 2022 11:59 PM
        Calendar defaultEndTime = Calendar.getInstance();
        defaultEndTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),23,59,0);

        // locations from firebase
        getLocationsFromFirebase(defaultStartTime.getTimeInMillis(),defaultEndTime.getTimeInMillis());
    }

    private void getLocationsFromFirebase(long startTimeInMilliSec, long endTimeInMilliSec) {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(email)
                .collection(Constants.LOCATION_COLLECTION)
                .whereGreaterThanOrEqualTo(Constants.LOC_TIMESTAMP,startTimeInMilliSec)
                .whereLessThanOrEqualTo(Constants.LOC_TIMESTAMP,endTimeInMilliSec)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot doc: queryDocumentSnapshots) {
                        locationInfoList.add(new LocationInfo(doc.getDouble(Constants.LAT).toString(),
                                doc.getDouble(Constants.LNG).toString(),
                                doc.getString(Constants.LOC_ADDRESS),
                                doc.getLong(Constants.LOC_TIMESTAMP)));
                    }

                    if(locationInfoList.size() == 0)
                    {
                        // setting progress bar visibility to GONE
                        binding.progressBar.setVisibility(View.GONE);

                        Toast.makeText(this, "No Location(s) found.", Toast.LENGTH_SHORT).show();
                    }
                    else if(locationInfoList.size() > 0)
                    {
                        // setting progress bar visibility to GONE
                        binding.progressBar.setVisibility(View.GONE);

                        // setting the markers on map
                        setMarkers(locationInfoList);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.i(TAG, "error getting locations: " + e.getMessage());

                    // setting progress bar visibility to GONE
                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(this, "Error getting location(s) info.", Toast.LENGTH_LONG).show();
                });

    }

    private void setMarkers(List<LocationInfo> locationInfoList) {

        if(mGoogleMap != null) {

            //setting the map type from preference
            int mapTypePreference = SharedPreference.getMapType();

            switch (mapTypePreference) {

                case 0:
                    //map type is default/normal
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;

                case 1:
                    //map type is satellite
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;

                case 2:
                    //map type is hybrid
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;

                case 3:
                    //map type is terrain
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
            }

            // removes marker if (any) on map
            mGoogleMap.clear();

            for(LocationInfo item : locationInfoList) {

                if(item.getLat() != null && item.getLng() != null) {

                    LatLng latLng = new LatLng(Double.parseDouble(item.getLat()) ,Double.parseDouble(item.getLng()));

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    mGoogleMap.moveCamera(cameraUpdate);
                    mGoogleMap.animateCamera(cameraUpdate);

                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(String.valueOf(userFirstName.charAt(0)))))
                            .title(Commons.timeInMilliToDateFormat(String.valueOf(item.getLocTimestamp())))
                            .snippet(item.getLocAddress())
                            .anchor((float) 0.5,(float) 0.5));
                }
            }
        }

    }

    private Bitmap getMarkerBitmapFromView(String userNameFirstLetter) {

        CustomMarkerBinding markerBinding = CustomMarkerBinding.inflate(getLayoutInflater());
        markerBinding.getRoot().measure(0,0);
        markerBinding.getRoot().layout(0,0,markerBinding.getRoot().getMeasuredWidth(),markerBinding.getRoot().getMeasuredHeight());

        // setting the user first name char
        markerBinding.textUserNameLetter.setText(userNameFirstLetter);

        Bitmap bitmap = Bitmap.createBitmap(markerBinding.getRoot().getMeasuredWidth(),markerBinding.getRoot().getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        markerBinding.getRoot().draw(new Canvas(bitmap));
        return bitmap;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    private void showCalendarDialog() {

        calendarViewDialog = new Dialog(this);

        LayoutCalendarViewBinding dialogBinding = LayoutCalendarViewBinding.inflate(getLayoutInflater());
        calendarViewDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.calendarView.setOnDateChangeListener((calendarView1, year, month, day) -> {

            // clears the locations list
            locationInfoList.clear();
            mGoogleMap.clear();

            // sets the selected date time to 12 AM. Like for 30th Dec 2022, 'startTime' will be 30th Dec 2022 12:00 AM
            Calendar startTime = Calendar.getInstance();
            startTime.set(year, month, day,0,0,0);

            // sets the selected date time to 11:59 PM. Like for 30th Dec 2022, 'endTime' will be 30th Dec 2022 11:59 PM
            Calendar endTime = Calendar.getInstance();
            endTime.set(year,month,day,23,59,0);

            getLocationsFromFirebase(startTime.getTimeInMillis(),endTime.getTimeInMillis());

            // dismiss the calendar view dialog
            calendarViewDialog.dismiss();

            // setting progress bar visibility to VISIBLE
            binding.progressBar.setVisibility(View.VISIBLE);

        });

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        calendarViewDialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        calendarViewDialog.show();
    }

}