package com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.model.Features;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SubscriptionViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Features>> featuresMutableLiveData;
    private FirebaseFirestore firestore;
    private ArrayList<Features> features;

    public SubscriptionViewModel() {
        featuresMutableLiveData = new MutableLiveData<>();
        firestore = FirebaseFirestore.getInstance();
        features = new ArrayList<>();
    }

    public MutableLiveData<ArrayList<Features>> getFeaturesMutableLiveData() {
        return featuresMutableLiveData;
    }

    public void getDataFirestoreDatabase() {

        firestore.collection("features").get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            firestore.collection("features").document(snapshot.getId()).get()
                                    .addOnSuccessListener(documentSnapshot -> {

                                        features.add(new Features((String) Objects.requireNonNull(documentSnapshot.getData()).get("image"),
                                                (String) Objects.requireNonNull(documentSnapshot.getData()).get("heading"),
                                                (String) Objects.requireNonNull(documentSnapshot.getData()).get("text")));

                                        Log.d("getDataFirestoreDatabase: ", String.valueOf(Objects.requireNonNull(documentSnapshot.getData()).get("image")));

                                        featuresMutableLiveData.setValue(features);

                                    });
                        }

                    }
                })
                .addOnFailureListener(e -> {

                    Log.d("getDataFirestoreDatabase: ", e.getMessage());
                    featuresMutableLiveData.setValue(null);

                });
    }
}
