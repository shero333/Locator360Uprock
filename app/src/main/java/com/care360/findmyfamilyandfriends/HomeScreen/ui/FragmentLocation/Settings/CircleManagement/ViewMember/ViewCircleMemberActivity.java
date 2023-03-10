package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.CircleManagement.ViewMember;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.CircleManagement.RemoveMember.MemberModel;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityViewCircleMemberBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewCircleMemberActivity extends AppCompatActivity {

    ActivityViewCircleMemberBinding binding;

    List<MemberModel> memberList = new ArrayList<>();
    List<String> emailList = new ArrayList<>();


    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityViewCircleMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this);

        adRequest = new AdRequest.Builder().build();

        setAd();

        binding.bannerAd.loadAd(adRequest);

        // toolbar
        binding.toolbarViewMembers.setNavigationOnClickListener(v -> onBackPressed());

        // get members
        getMembersData();
    }

    @SuppressWarnings("unchecked")
    private void getMembersData() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(SharedPreference.getCircleAdminId())
                .collection(Constants.CIRCLE_COLLECTION)
                .document(SharedPreference.getCircleId())
                .get()
                .addOnSuccessListener(doc -> {

                    ArrayList<String> list = (ArrayList<String>) doc.get(Constants.CIRCLE_MEMBERS);

                    emailList.addAll(list);

                    for(int i = 0; i < emailList.size(); i++)
                    {
                        int loopIndex = i;

                        // getting the full name of user
                        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                .document(emailList.get(i))
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    String name= documentSnapshot.getString(Constants.FIRST_NAME).concat(" ").concat(documentSnapshot.getString(Constants.LAST_NAME));

                                    memberList.add(new MemberModel(name, emailList.get(loopIndex)));

                                    // on last iteration, sets data to recyclerview
                                    if(loopIndex == emailList.size() - 1) {

                                        // setting the recyclerview
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewCircleMemberActivity.this,LinearLayoutManager.VERTICAL,false);
                                        binding.recyclerDeleteMembers.setLayoutManager(layoutManager);

                                        ViewMemberAdapter adapter = new ViewMemberAdapter(ViewCircleMemberActivity.this, memberList);
                                        binding.recyclerDeleteMembers.setAdapter(adapter);
                                    }
                                });
                    }
                });

    }

    private void setAd() {

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {

                        Log.d("AdError", adError.toString());
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d("AdError", "Ad was loaded.");
                        mInterstitialAd = interstitialAd;
                    }
                });
    }

}