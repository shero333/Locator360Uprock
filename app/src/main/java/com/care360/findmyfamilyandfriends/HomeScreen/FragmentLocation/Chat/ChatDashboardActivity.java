package com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Filter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Chat.Model.UserInfo;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityChatDashboardBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChatDashboardActivity extends AppCompatActivity implements ChatDashboardAdapter.OnChatMemberListener {

    private static final String TAG = "CHAT_DASHBOARD";

    ActivityChatDashboardBinding binding;

    //members info list
    List<UserInfo> membersInfoList = new ArrayList<>();

    //members list
    List<String> circleMembersList = new ArrayList<>();

    //adapter
    ChatDashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityChatDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        adapter = new ChatDashboardAdapter(this, membersInfoList, this);

        //get the circle members list
        getCirclesMemberList();

        // edit text search
        binding.editTextSearch.addTextChangedListener(searchMemberTextWatcher);

    }

    private final TextWatcher searchMemberTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Filter membersFilter = new Filter() {
                @Override
                public FilterResults performFiltering(CharSequence charSequence) {

                    List<UserInfo> filteredMemberList = new ArrayList<>();

                    if (charSequence == null || charSequence.length() == 0) {
                        filteredMemberList.addAll(adapter.membersFilteringList);
                    } else {
                        String filterPattern = charSequence.toString().trim().toLowerCase();

                        for (UserInfo member : adapter.membersFilteringList) {
                            if (member.getUserFullName().toLowerCase().contains(filterPattern)) {
                                filteredMemberList.add(member);
                            }
                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredMemberList;
                    return filterResults;
                }

                @SuppressLint("NotifyDataSetChanged")
                @SuppressWarnings("unchecked")
                @Override
                public void publishResults(CharSequence charSequence, FilterResults filterResults) {

                    adapter.membersList.clear();
                    adapter.membersList.addAll((List<UserInfo>) filterResults.values);
                    adapter.notifyDataSetChanged();

                }
            };

            membersFilter.filter(charSequence);

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @SuppressWarnings("unchecked")
    private void getCirclesMemberList() {

        // current user email
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                .whereArrayContains(Constants.CIRCLE_MEMBERS, currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot doc : task.getResult()) {

                            ArrayList<String> members = (ArrayList<String>) doc.get(Constants.CIRCLE_MEMBERS);
                            circleMembersList.addAll(members);
                        }

                        // removes all the occurrences of current user email (if any) from list
                        circleMembersList.removeAll(Collections.singleton(currentUserEmail));

                        if(circleMembersList.size() > 0) {

                            //hides the no members view
                            binding.consNoMembers.setVisibility(View.GONE);

                            binding.recyclerView.setVisibility(View.VISIBLE);

                            // retrieve the details of all members
                            for(int i=0; i < circleMembersList.size(); i++) {

                                //fetch the user info
                                int loopIndex = i;

                                FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                        .document(circleMembersList.get(i))
                                        .get()
                                        .addOnSuccessListener(doc -> {

                                            String fullName = doc.getString(Constants.FIRST_NAME).concat(" ").concat(doc.getString(Constants.LAST_NAME));

                                            membersInfoList.add(new UserInfo(circleMembersList.get(loopIndex), doc.getString(Constants.FCM_TOKEN),fullName, doc.getString(Constants.IMAGE_PATH)));

                                            //setting recyclerview
                                            if(loopIndex == circleMembersList.size() -1 ) {

                                                //recyclerview
                                                setRecyclerView(membersInfoList);

                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "error getting member details: " + e.getMessage()));
                            }
                        }
                        else if(circleMembersList.size() == 0) {

                            //show the no members view
                            binding.consNoMembers.setVisibility(View.VISIBLE);
                            binding.recyclerView.setVisibility(View.GONE);

                        }
                    }

                })
                .addOnFailureListener(e -> Log.e(TAG, "error getting members list:" + e.getMessage()));

    }

    private void setRecyclerView(List<UserInfo> membersInfoList) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatDashboardAdapter(this, membersInfoList, this);
        binding.recyclerView.setAdapter(adapter);

        //if the activity is called from notification in case a new message is received
        Intent intent = getIntent();
        if(intent.getStringExtra(Constants.SENDER_ID) != null) {

            for(int i=0; i < membersInfoList.size(); i++) {
                if(intent.getStringExtra(Constants.SENDER_ID).equals(membersInfoList.get(i).getUserId())) {

                    Intent chatIntent = new Intent(this, ChatDetailActivity.class);
                    chatIntent.putExtra(Constants.KEY_USER_INFO,membersInfoList.get(i));
                    chatIntent.putExtra(Constants.IS_APP_IN_FOREGROUND,intent.getBooleanExtra(Constants.IS_APP_IN_FOREGROUND,true));
                    chatIntent.putExtra(Constants.RANDOM_COLOR, Commons.randomColor());
                    startActivity(chatIntent);
                }
            }
        }
    }

    //member click listener
    @Override
    public void onChatMemberClick(int position,int randomColor) {

        Intent intent = new Intent(this, ChatDetailActivity.class);
        intent.putExtra(Constants.KEY_USER_INFO,membersInfoList.get(position));
        intent.putExtra(Constants.IS_APP_IN_FOREGROUND,true);
        intent.putExtra(Constants.RANDOM_COLOR,randomColor);
        startActivity(intent);
    }

}