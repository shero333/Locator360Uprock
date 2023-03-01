package com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.databinding.CustomMarkerBinding;
import com.care360.findmyfamilyandfriends.databinding.FragmentLocationBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutBottomSheetMapTypeBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.Application.App;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.AddMember.AddMemberActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Battery.BatteryStatusModelClass;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.BottomSheetMembers.BottomSheetMemberAdapter;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.BottomSheetMembers.MemberDetail;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.BottomSheetMembers.MemberLocationActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.ChatDashboardActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.CreateCircle.CreateCircleMainActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.JoinCircle.CircleModel;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.JoinCircle.CircleToolbarAdapter;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.JoinCircle.JoinCircleMainActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.SettingsActivity;
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.WorkManager.CircleExpiryDateWorker;
import com.care360.findmyfamilyandfriends.WorkManager.LocationUpdateWorker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LocationFragment extends Fragment implements OnMapReadyCallback, CircleToolbarAdapter.OnToolbarCircleClickListener, LocationListener, BottomSheetMemberAdapter.OnAddedMemberClickInterface, BottomSheetMemberAdapter.OnAddNewMemberInterface {
    private static final String TAG = "FRAG_LOCATION";
    private FragmentLocationBinding binding;

    //show and hide extended toolbar view animations
    Animation showToolbarExtAnim, hideToolbarExtAnim;

    // circle list
    List<CircleModel> circleList = new ArrayList<>();

    // user detail list including last know location, battery status, personal info etc
    List<MemberDetail> membersDetailList = new ArrayList<>();

    private FusedLocationProviderClient mLocationClient;

    private GoogleMap mGoogleMap;

    private Location location;

    //recyclerview of extended toolbar
    private RecyclerView circleSelectionRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing view binding
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //initializing map
        initializeMap();

        // this condition will save location update to firebase for 1 time throughout the application lifecycle.
        if (!App.IS_LOCATION_UPDATE_SAVED_TO_FIREBASE) {
            //checking location permission
            checkLocationPermission();

            // updates the value from false to true
            App.IS_LOCATION_UPDATE_SAVED_TO_FIREBASE = true;
        }

        // fetch firebase data
        getDetailDataFromFirebase();

        //recyclerview initialization
        circleSelectionRecyclerView = binding.toolbarExtendedView.recyclerViewCircle;

        //items click listener
        clickListeners();

        //work manager for updating user location every hour
        periodicLocationUpdated();

        //work manager for checking circle code expiry date every 8 hours
        periodicCircleCodeChecker();

        return view;
    }

    private void periodicLocationUpdated() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest periodicLocationUpdateRequest = new PeriodicWorkRequest.Builder(LocationUpdateWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints).build();

        WorkManager.getInstance(requireActivity().getApplicationContext())
                .enqueueUniquePeriodicWork("locUpdate", ExistingPeriodicWorkPolicy.KEEP, periodicLocationUpdateRequest);

    }

    private void periodicCircleCodeChecker() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest periodicCircleCodeRequest = new PeriodicWorkRequest.Builder(CircleExpiryDateWorker.class, 8, TimeUnit.HOURS)
                .setConstraints(constraints).build();

        WorkManager.getInstance(requireActivity().getApplicationContext())
                .enqueueUniquePeriodicWork("circleInfoUpdate", ExistingPeriodicWorkPolicy.KEEP, periodicCircleCodeRequest);
    }

    private void initializeMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //when this listener is called, the live location icon visibility will be set to VISIBLE
        mGoogleMap.setOnCameraMoveListener(() -> binding.consLiveLoc.setVisibility(View.VISIBLE));
    }

    @SuppressLint({"MissingPermission", "InlinedApi"})
    private void checkLocationPermission() {

        if (Permissions.hasLocationPermission(requireContext())) {

            Log.i(TAG, "checkLocationPermission() : app has location permission");

            Commons.isGpsEnabled(requireActivity(), isSuccessful -> {

                if (isSuccessful) {
                    Log.i(TAG, "checkLocationPermission() : gps already enabled");

                    //fetch the location
                    getLocationThroughLastKnownApproach();
                }
                else if (!isSuccessful) {
                    Log.i(TAG, "checkLocationPermission() : gps not enabled");

                    //displays the built in dialog
                    googleDefaultGPSDialog();
                }
            });

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.REQUEST_CODE_LOCATION);
        }
    }

    private void googleDefaultGPSDialog() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000).setFastestInterval(3000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> responseTask = LocationServices.getSettingsClient(getActivity().getApplicationContext())
                .checkLocationSettings(locationBuilder.build());

        responseTask.addOnCompleteListener(task -> {

            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);

                // gps is already enabled
                Log.i(TAG, "googleDefaultGPSDialog() : gps is already enabled");

            }
            catch (ApiException e) {
                if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;

                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(resolvableApiException.getResolution()).build();
                    gpsResultLauncher.launch(intentSenderRequest);
                }
            }
        });

    }

    ActivityResultLauncher<IntentSenderRequest> gpsResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {

        if (result.getResultCode() == RESULT_OK)
        {
            Log.i(TAG, "gpsResultLauncher : gps permission allowed");
            //fetch the location
            checkLocationPermission();
        }
        else {
            //show the dialog again
            Commons.isGpsEnabled(requireActivity(), isSuccessful -> {

                if (!isSuccessful) {
                    Log.i(TAG, "gpsResultLauncher : gps permission denied");
                    //displays the built in dialog
                    googleDefaultGPSDialog();
                }
            });
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG, "onRequestPermissionsResult() : permission granted");

                //getting the current location
                checkLocationPermission();
            }
            else
            {
                Log.i(TAG, "onRequestPermissionsResult() : permission denied");

                //navigate to app settings screen to allow permission from settings
                Commons.locationPermissionDialog(requireActivity(), isSuccessful -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"
                            + BuildConfig.APPLICATION_ID));
                    locationResultLauncher.launch(intent);
                });
            }
        }
    }

    ActivityResultLauncher<Intent> locationResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.i(TAG, "locationResultLauncher: ");

        // checks permission again, if is allowed
        checkLocationPermission();
    });

    @SuppressLint("MissingPermission")
    private void getLocationThroughLastKnownApproach() {

        Log.i(TAG, "getLocationThroughLastKnownApproach() ");

        // this function will call LocationListener overridden method when location changes
        startLocationUpdates();

        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                location = task.getResult();

                if (location != null) {

                    Log.i(TAG, "getLocationThroughLastKnownApproach() -> location != null");

                    // saves the location update in firebase
                    saveLocationInFirebase(location);

                }
                else if(location == null) {
                    Log.i(TAG, "getLocationThroughLastKnownApproach() -> location == null");

                    /*
                        if task result returned has null location (case like when gps is turned on, it will first return null and after sometime location won't be null)
                        if that's the case, then will get location through mLocationClient.getCurrentLocation (currentLocationRequest, cancellationToken)
                    */
                    getLocationThroughCurrentLocationApproach();
                }
            }

        });
    }

    @SuppressLint("MissingPermission")
    private void getLocationThroughCurrentLocationApproach() {

        Log.i(TAG, "getLocationThroughCurrentLocationApproach()");

        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder().build();

        CancellationToken cancellationToken = new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        };

        mLocationClient.getCurrentLocation(currentLocationRequest, cancellationToken).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                location = task.getResult();

                if (location != null) {
                    Log.i(TAG, "getLocationThroughCurrentLocationApproach() -> location != null");

                    // saves the location update in firebase
                    saveLocationInFirebase(location);
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        Log.i(TAG, "startLocationUpdates()");

        //location manager method
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // 60000 milliseconds = 1 minute
        // 1000 milliseconds = 1 second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);
    }

    // LocationListener overridden method
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, "onLocationChanged()");

        // saves the location in firebase firestore
        saveLocationInFirebase(location);
    }

    private void saveLocationInFirebase(Location location) {

        Context context = App.getAppContext();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        BatteryStatusModelClass batteryStatus = Commons.getCurrentBatteryStatus(context);

        // location data
        Map<String, Object> locData = new HashMap<>();

        locData.put(Constants.LAT, location.getLatitude());
        locData.put(Constants.LNG, location.getLongitude());
        locData.put(Constants.LOC_ADDRESS, Commons.getLocationAddress(context, location));
        locData.put(Constants.IS_PHONE_CHARGING, batteryStatus.isCharging());
        locData.put(Constants.BATTERY_PERCENTAGE, batteryStatus.getBatteryPercentage());
        locData.put(Constants.LOC_TIMESTAMP, /*String.valueOf(*/System.currentTimeMillis()/*)*/);

        // the location info in LOCATION collection
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(Objects.requireNonNull(currentUserEmail))
                .collection(Constants.LOCATION_COLLECTION)
                .document()
                .set(locData)
                .addOnSuccessListener(unused -> Log.i(TAG, "Firestore location update successful in LOCATION COLLECTION"))
                .addOnFailureListener(e -> Log.e(TAG, "Error. Firestore location update in LOCATION COLLECTION" + e.getMessage()));

        // updates the values of location in USER collection
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUserEmail)
                .update(locData)
                .addOnSuccessListener(unused -> Log.i(TAG, " successful location updated in USER collection"))
                .addOnFailureListener(e -> Log.e(TAG, "error. updating location data in USER collection: " + e.getMessage()));

    }

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    private void getDetailDataFromFirebase() {

        //current user email
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                .whereArrayContains(Constants.CIRCLE_MEMBERS, currentUserEmail)
                .addSnapshotListener((value, error) -> {

                    // clearing the circle and members list
                    circleList.clear();
                    membersDetailList.clear();

                    if (value != null) {

                        for (DocumentSnapshot doc : value) {

                            circleList.add(new CircleModel(doc.getId(), Objects.requireNonNull(doc.get(Constants.CIRCLE_ADMIN)).toString(), doc.getString(Constants.CIRCLE_NAME),
                                    (List<String>) doc.get(Constants.CIRCLE_MEMBERS), doc.getString(Constants.CIRCLE_JOIN_CODE)));
                        }

                        if (SharedPreference.getCircleId().equals(Constants.NULL)) {

                            //setting the circle as default
                            SharedPreference.setCircleId(circleList.get(0).getCircleId());
                            SharedPreference.setCircleAdminId(circleList.get(0).getCircleOwnerId());
                            SharedPreference.setCircleName(circleList.get(0).getCircleName());
                            SharedPreference.setCircleInviteCode(circleList.get(0).getCircleJoinCode());

                            if (getContext() != null) {

                                // setting the circle name to toolbar & toolbar extended view
                                binding.toolbar.textViewCircleName.setText(SharedPreference.getCircleName());
                                binding.toolbarExtendedView.txtCircleName.setText(SharedPreference.getCircleName());
                            }

                            // hash map is used here to remove duplication when location data is updated.
                            HashMap<String,MemberDetail> hashMap = new HashMap<>();

                            // getting the members
                            for (String memberEmail : circleList.get(0).getCircleMembersList())
                            {
                                // getting the user info
                                FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                        .document(memberEmail)
                                        .addSnapshotListener((valueUserInfo, errorUserInfo) -> {

                                            MemberDetail memberDetail = new MemberDetail();

                                            memberDetail.setMemberFirstName(valueUserInfo.getString(Constants.FIRST_NAME));
                                            memberDetail.setMemberLastName(valueUserInfo.getString(Constants.LAST_NAME));
                                            memberDetail.setMemberImageUrl(valueUserInfo.getString(Constants.IMAGE_PATH));
                                            memberDetail.setMemberEmail(valueUserInfo.getString(Constants.EMAIL));

                                            if(valueUserInfo.getDouble(Constants.LAT) != null  && valueUserInfo.getDouble(Constants.LNG) != null)
                                            {
                                                memberDetail.setLocationLat((valueUserInfo.getDouble(Constants.LAT).toString()));
                                                memberDetail.setLocationLng((valueUserInfo.getDouble(Constants.LNG).toString()));
                                                memberDetail.setLocationAddress(valueUserInfo.getString(Constants.LOC_ADDRESS));
                                                memberDetail.setLocationTimestamp(Long.parseLong(String.valueOf(valueUserInfo.get(Constants.LOC_TIMESTAMP))));
                                                memberDetail.setBatteryPercentage(Math.toIntExact(valueUserInfo.getLong(Constants.BATTERY_PERCENTAGE)));
                                                memberDetail.setPhoneCharging(valueUserInfo.getBoolean(Constants.IS_PHONE_CHARGING));
                                            }
                                            if(hashMap.containsKey(memberEmail)) {
                                                hashMap.replace(memberEmail,memberDetail);
                                            }
                                            else {
                                                hashMap.put(memberEmail,memberDetail);
                                            }

                                            // members recyclerview
                                            setBottomSheetMembersRecyclerView(hashMap);

                                        });
                            }
                        }
                        else if (!SharedPreference.getCircleId().equals(Constants.NULL)) {

                            // setting the circle name to toolbar & toolbar extended view
                            if (getContext() != null) {
                                binding.toolbar.textViewCircleName.setText(SharedPreference.getCircleName());
                                binding.toolbarExtendedView.txtCircleName.setText(SharedPreference.getCircleName());
                            }

                            // hash map is used here to remove duplication when location data is updated.
                            HashMap<String,MemberDetail> hashMap = new HashMap<>();

                            // if preference is not null, it means that any circle is selected as default
                            for (CircleModel circleModel : circleList)
                            {
                                if (circleModel.getCircleId().equals(SharedPreference.getCircleId()))
                                {
                                    //sets circle name & join code in Shared pref
                                    SharedPreference.setCircleAdminId(circleModel.getCircleOwnerId());
                                    SharedPreference.setCircleName(circleModel.getCircleName());
                                    SharedPreference.setCircleInviteCode(circleModel.getCircleJoinCode());

                                    for (String memberEmail : circleModel.getCircleMembersList())
                                    {
                                        // getting the user info
                                        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                                .document(memberEmail)
                                                .addSnapshotListener((valueUserInfo, errorUserInfo) -> {
                                                    MemberDetail memberDetail = new MemberDetail();

                                                    memberDetail.setMemberFirstName(valueUserInfo.getString(Constants.FIRST_NAME));
                                                    memberDetail.setMemberLastName(valueUserInfo.getString(Constants.LAST_NAME));
                                                    memberDetail.setMemberImageUrl(valueUserInfo.getString(Constants.IMAGE_PATH));
                                                    memberDetail.setMemberEmail(valueUserInfo.getString(Constants.EMAIL));

                                                    if(valueUserInfo.getDouble(Constants.LAT) != null  && valueUserInfo.getDouble(Constants.LNG) != null)
                                                    {
                                                        memberDetail.setLocationLat((valueUserInfo.getDouble(Constants.LAT).toString()));
                                                        memberDetail.setLocationLng((valueUserInfo.getDouble(Constants.LNG).toString()));
                                                        memberDetail.setLocationAddress(valueUserInfo.getString(Constants.LOC_ADDRESS));
                                                        memberDetail.setLocationTimestamp(Long.parseLong(String.valueOf(valueUserInfo.get(Constants.LOC_TIMESTAMP))));
                                                        memberDetail.setBatteryPercentage(Math.toIntExact(valueUserInfo.getLong(Constants.BATTERY_PERCENTAGE)));
                                                        memberDetail.setPhoneCharging(valueUserInfo.getBoolean(Constants.IS_PHONE_CHARGING));
                                                    }

                                                    if(hashMap.containsKey(memberEmail)) {
                                                        hashMap.replace(memberEmail,memberDetail);
                                                    }
                                                    else {
                                                        hashMap.put(memberEmail,memberDetail);
                                                    }

                                                    // members recyclerview
                                                    setBottomSheetMembersRecyclerView(hashMap);
                                                });
                                    }
                                }
                            }
                        }

                        // setting the circle recyclerview
                        selectCircleRecyclerview();
                    }
                });
    }

    private void loadAnimations() {

        //show toolbar extended view animation
        showToolbarExtAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.top_anim);

        //hide toolbar extended view animation
        hideToolbarExtAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_anim);
    }

    private void clickListeners() {

        //toolbar settings click listener
        binding.toolbar.consSettingToolbar.setOnClickListener(v -> toolbarSettings());

        //toolbar messages click listener
        binding.toolbar.consChatToolbar.setOnClickListener(v -> toolbarChat());

        //toolbar circle name click listener
        binding.toolbar.consCircle.setOnClickListener(v -> circleExtendedView());

        //check-in click listener
        binding.consCheckIn.setOnClickListener(v -> Toast.makeText(getContext(), "Check In", Toast.LENGTH_SHORT).show());

        //map type click listener
        binding.consMapType.setOnClickListener(v -> bottomSheetMapType());

        //navigate to live location
        binding.consLiveLoc.setOnClickListener(v -> {

            //moves to current location
            checkLocationPermission();

            //after clicking and moving to current location, hides the live location view
            binding.consLiveLoc.setVisibility(View.GONE);
        });

        //click listener of the extended toolbar view (circle selection view)
        extendedToolbarViewClickListeners();

        //bottom sheet containing info about the current circle members
        bottomSheetMembers();
    }

    private void toolbarSettings() {
        startActivity(new Intent(getActivity(), SettingsActivity.class));

        //slide up animation of Settings activity
        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
    }

    private void toolbarChat() {
        startActivity(new Intent(getActivity(), ChatDashboardActivity.class));
    }

    private void extendedToolbarViewClickListeners() {

        //add member click listener
        binding.toolbarExtendedView.imgViewAddCircleMembers.setOnClickListener(v -> addCircleMember(true));

        //circle name click listener
        binding.toolbarExtendedView.consCircleName.setOnClickListener(v -> circleShrunkView());

        //button create circle click listener
        binding.toolbarExtendedView.btnCreateCircleToolbar.setOnClickListener(v -> createNewCircle());

        //button join circle click listener
        binding.toolbarExtendedView.btnJoinCircleToolbar.setOnClickListener(v -> joinCircle());

        // if this view is clicked, the extended view visibility will be set to gone
        binding.toolbarExtendedView.backgroundOpaqueView.setOnClickListener(v -> circleShrunkView());
    }

    //we have add member button in extended toolbar view and bottom navigation as well
    private void addCircleMember(boolean isToolbarAddMemberBtnClicked) {

        Intent intent = new Intent(getActivity(), AddMemberActivity.class);
        intent.putExtra(Constants.ADD_MEMBER_BUTTON_CLICKED, isToolbarAddMemberBtnClicked);
        addMemberResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> addMemberResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK) {

            Intent intent = result.getData();

            if (intent != null) {

                boolean isToolbarAddMemberBtnClicked = intent.getBooleanExtra(Constants.ADD_MEMBER_BUTTON_CLICKED, false);

                if (isToolbarAddMemberBtnClicked) {

                    //delay the shrunk to give an animation type look
                    delayCircleShrunkView();

                } else {

                    //collapse the bottom navigation
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View bottomSheet = binding.bottomSheetMembers.consBottomSheet;
                            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }, 300);

                }

            }
        }

    });

    private void createNewCircle() {
        createNewCircleResultLauncher.launch(new Intent(requireActivity(), CreateCircleMainActivity.class));
    }

    ActivityResultLauncher<Intent> createNewCircleResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK) {

            //delay the shrunk to give an animation type look
            delayCircleShrunkView();

            Intent intent = result.getData();

            if (intent != null) {

                boolean isCircleCreated = intent.getBooleanExtra(Constants.IS_CIRCLE_CREATED, false);

                if (isCircleCreated) {

                    // setting the newly created circle name to toolbar & extended view
                    binding.toolbar.textViewCircleName.setText(SharedPreference.getCircleName());
                    binding.toolbarExtendedView.txtCircleName.setText(SharedPreference.getCircleName());

                    //setting the data
                    getDetailDataFromFirebase();

                    Toast.makeText(requireContext(), "Circle Created Successfully.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    });

    private void joinCircle() {
        joinCircleResultLauncher.launch(new Intent(requireActivity(), JoinCircleMainActivity.class));
    }

    ActivityResultLauncher<Intent> joinCircleResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK) {

            Intent intent = result.getData();

            if (intent != null) {

                //delay the shrunk to give an animation type look
                delayCircleShrunkView();

                //sets the joined circle name onto toolbar & extended toolbar view
                binding.toolbar.textViewCircleName.setText(SharedPreference.getCircleName());
                binding.toolbarExtendedView.txtCircleName.setText(SharedPreference.getCircleName());

                // setting the data
                getDetailDataFromFirebase();

            }
        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            //when back pressed is called from 'JoinCircleMainActivity'
            delayCircleShrunkView();
        }

    });

    //delays the extended toolbar view to give it an animation like flow when Activity Result Launchers are called
    private void delayCircleShrunkView() {

        new Handler().postDelayed(this::circleShrunkView, 300);
    }

    private void circleShrunkView() {

        //loading the toolbar extended view animations
        loadAnimations();

        binding.toolbarExtendedView.consCircleSelection.setAnimation(hideToolbarExtAnim);

        //the background of extended view visibility is set to gone to set the hide extended toolbar animation
        binding.toolbarExtendedView.backgroundOpaqueView.setVisibility(View.GONE);

        //setting visibility to gone after 300 millisecond
        new Handler().postDelayed(() -> binding.toolbarExtendedView.consCircleSelection.setVisibility(View.GONE), 300);

    }

    public void circleExtendedView() {

        //loading the toolbar extended view animations
        loadAnimations();

        //setting the visibility of extended view to visible
        binding.toolbarExtendedView.backgroundOpaqueView.setVisibility(View.VISIBLE);

        //setting the background of extended view visibility to visible
        binding.toolbarExtendedView.consCircleSelection.setVisibility(View.VISIBLE);

        //setting the animation
        binding.toolbarExtendedView.consCircleSelection.setAnimation(showToolbarExtAnim);

    }

    private void selectCircleRecyclerview() {

        if (getContext() != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
            circleSelectionRecyclerView.setLayoutManager(layoutManager);

            CircleToolbarAdapter adapterToolbar = new CircleToolbarAdapter(requireContext(), circleList, this);
            circleSelectionRecyclerView.setAdapter(adapterToolbar);
        }
    }

    //toolbar circle name recyclerview click listener
    @Override
    public void onCircleSelected(int position) {

        // setting the updated circle name to toolbar & extended view
        binding.toolbar.textViewCircleName.setText(SharedPreference.getCircleName());
        binding.toolbarExtendedView.txtCircleName.setText(SharedPreference.getCircleName());

        // setting the values to shared preference
        SharedPreference.setCircleId(circleList.get(position).getCircleId());
        SharedPreference.setCircleName(circleList.get(position).getCircleName());
        SharedPreference.setCircleInviteCode(circleList.get(position).getCircleJoinCode());

        //clears the members list
        membersDetailList.clear();

        //get the details of particular circle
        getDetailDataFromFirebase();
    }

    private void bottomSheetMembers() {

        //binding.bottomSheetMembers.consPlaces.setOnClickListener(v -> Toast.makeText(getContext(), "Places", Toast.LENGTH_SHORT).show());
    }

    private void setBottomSheetMembersRecyclerView(HashMap<String,MemberDetail> hashMap) {

        //purpose of this condition is to remove null pointer exception. (If you're in Safety Fragment & location changes)
        if (getContext() != null) {

            // clearing the list first
            membersDetailList.clear();

            // converting the hashmap to list
            membersDetailList.addAll(hashMap.values());

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.bottomSheetMembers.recyclerBottomSheetMember.setLayoutManager(layoutManager);
            binding.bottomSheetMembers.recyclerBottomSheetMember.setAdapter(new BottomSheetMemberAdapter(requireContext(), membersDetailList, this, this));

            // set the markers on map
            setMarkers(membersDetailList);
        }
    }

    private void setMarkers(List<MemberDetail> memberDetailsList) {

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
    
            for(MemberDetail item : memberDetailsList)
            {
                if(item.getLocationLat() != null && item.getLocationLng() != null) {

                    LatLng latLng = new LatLng(Double.parseDouble(item.getLocationLat()) ,Double.parseDouble(item.getLocationLng()));

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    mGoogleMap.moveCamera(cameraUpdate);
                    mGoogleMap.animateCamera(cameraUpdate);

                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(String.valueOf(item.getMemberFirstName().charAt(0)))))
                            .title(item.getMemberFirstName().concat(" ").concat(item.getMemberLastName()))
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

    // recyclerview bottom sheet member 'Add new member' click listener
    @Override
    public void onAddNewMemberClicked() {
        addCircleMember(false);
    }

    // recyclerview bottom sheet member click listener
    @Override
    public void onAddedMemberClicked(int position) {

        Intent intent = new Intent(getActivity(), MemberLocationActivity.class);
        intent.putExtra(Constants.EMAIL,membersDetailList.get(position).getMemberEmail());
        intent.putExtra(Constants.FIRST_NAME,membersDetailList.get(position).getMemberFirstName());
        startActivity(intent);
    }

    private void bottomSheetMapType() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog);

        LayoutBottomSheetMapTypeBinding mapTypeBinding = LayoutBottomSheetMapTypeBinding.inflate(LayoutInflater.from(getContext()));

        bottomSheetDialog.setContentView(mapTypeBinding.getRoot());

        bottomSheetDialog.show();

        mapTypeBinding.consMapDefault.setOnClickListener(v -> setDefaultMapType(mapTypeBinding));

        mapTypeBinding.consMapSatellite.setOnClickListener(v -> setSatelliteMapType(mapTypeBinding));

        mapTypeBinding.consMapHybrid.setOnClickListener(v -> setHybridMapType(mapTypeBinding));

        mapTypeBinding.consMapTerrain.setOnClickListener(v -> setTerrainMapType(mapTypeBinding));

        // cancel click listener
        mapTypeBinding.imgViewCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // getting the preference value and setting value to map type
        int mapTypePref = SharedPreference.getMapType();

        switch (mapTypePref) {

            case 0:
                //map type is default/normal
                setDefaultMapType(mapTypeBinding);
                break;

            case 1:
                //map type is satellite
                setSatelliteMapType(mapTypeBinding);
                break;

            case 2:
                //map type is hybrid
                setHybridMapType(mapTypeBinding);
                break;

            case 3:
                //map type is terrain
                setTerrainMapType(mapTypeBinding);
                break;
        }

    }

    private void setDefaultMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(0);

        //change the map type
        if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //change the rest of views
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.black));

    }

    private void setSatelliteMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(1);

        //change the map type
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        //change the rest of views
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.black));

    }

    private void setHybridMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(2);

        //change the map type
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        //change the rest of views
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.black));

    }

    private void setTerrainMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(3);

        //change the map type
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

        //change the rest of views
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.black));

    }

    //nullifying binding object
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}