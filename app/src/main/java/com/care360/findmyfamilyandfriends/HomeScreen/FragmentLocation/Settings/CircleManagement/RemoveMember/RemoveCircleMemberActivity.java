package com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.CircleManagement.RemoveMember;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kl360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.kl360.findmyfamilyandfriends.Util.Constants;
import com.kl360.findmyfamilyandfriends.databinding.ActivityDeleteCircleMemberBinding;

import java.util.ArrayList;
import java.util.List;

public class RemoveCircleMemberActivity extends AppCompatActivity {

    ActivityDeleteCircleMemberBinding binding;

    List<MemberModel> memberList = new ArrayList<>();
    List<String> emailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityDeleteCircleMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // toolbar
        binding.toolbarDeleteCircleMembers.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbarDeleteCircleMembers.setTitle(SharedPreference.getCircleName());

        // get the members data
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
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(RemoveCircleMemberActivity.this,LinearLayoutManager.VERTICAL,false);
                                        binding.recyclerDeleteMembers.setLayoutManager(layoutManager);

                                        RemoveCircleMemberAdapter adapter = new RemoveCircleMemberAdapter(RemoveCircleMemberActivity.this, memberList);
                                        binding.recyclerDeleteMembers.setAdapter(adapter);
                                    }
                                });
                    }
                });

    }
}