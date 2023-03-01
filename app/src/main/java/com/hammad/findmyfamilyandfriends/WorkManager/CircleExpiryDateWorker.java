package com.hammad.findmyfamily.WorkManager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CircleExpiryDateWorker extends Worker {

    private static final String TAG = "CIRCLE_WORKER_CLASS";

    public CircleExpiryDateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // current user email
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        FirebaseFirestore.getInstance().collectionGroup(Constants.CIRCLE_COLLECTION)
                .whereArrayContains(Constants.CIRCLE_MEMBERS,email)
                .whereLessThanOrEqualTo(Constants.CIRCLE_CODE_EXPIRY_DATE,String.valueOf(System.currentTimeMillis()))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot doc: task.getResult())
                        {
                            String circleId = doc.getId();
                            String circleAdminEmail = doc.getString(Constants.CIRCLE_ADMIN);

                            Map<String,Object> updatedCircleData = new HashMap<>();
                            updatedCircleData.put(Constants.CIRCLE_JOIN_CODE, Commons.getRandomGeneratedCircleCode());
                            updatedCircleData.put(Constants.CIRCLE_CODE_EXPIRY_DATE,String.valueOf(System.currentTimeMillis()+259200000));

                            //updates the circle code expiry date and circle join code
                            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                                    .document(circleAdminEmail)
                                    .collection(Constants.CIRCLE_COLLECTION)
                                    .document(circleId)
                                    .update(updatedCircleData)
                                    .addOnSuccessListener(unused -> Log.i(TAG, "Circle code expiry date & join code updated."))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error updating circle values: "+e.getMessage()));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "error getting expired circle date: "+e.getMessage()));

        return Result.success();
    }
}
