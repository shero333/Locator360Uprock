package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.JoinCircle.CircleModel;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityCircleManagementBinding;

import java.util.List;
import java.util.Objects;

public class CircleManagementActivity extends AppCompatActivity {

    private static final String TAG = "CIR_MANAG_ACT";

    ActivityCircleManagementBinding binding;

    CircleModel currentCircleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityCircleManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarCircleManagement.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbarCircleManagement.setTitle(SharedPreference.getCircleName());

        // get current Cirlce info
        getCirlceInfo();
    }

    @SuppressWarnings("unchecked")
    private void getCirlceInfo() {

        FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                .whereEqualTo(Constants.CIRCLE_ID,SharedPreference.getCircleId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        currentCircleInfo = new CircleModel(doc.getId(), Objects.requireNonNull(doc.get(Constants.CIRCLE_ADMIN)).toString(), doc.getString(Constants.CIRCLE_NAME),
                                (List<String>) doc.get(Constants.CIRCLE_MEMBERS), doc.getString(Constants.CIRCLE_JOIN_CODE));
                    }



                })
                .addOnFailureListener(e -> Log.e(TAG, "error getting circle info: "+e.getMessage()));

    }
}