package com.hammad.findmyfamily.WorkManager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CircleExpiryDateWorker extends Worker {

    Context context;

    public CircleExpiryDateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        context = getApplicationContext();

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        return Result.success();
    }
}
