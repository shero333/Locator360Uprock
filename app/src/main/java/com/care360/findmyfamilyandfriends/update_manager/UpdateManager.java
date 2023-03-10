package com.care360.findmyfamilyandfriends.update_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

public class UpdateManager extends AppCompatActivity {

    public static int UPDATE_CODE=22;
    private AppUpdateManager updateManager;
    private Task<AppUpdateInfo> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateManager = AppUpdateManagerFactory.create(this);
        task = updateManager.getAppUpdateInfo();

        updateApp();

    }

    public void updateApp(){
        //listener
        task.addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){

                try {
                    updateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.FLEXIBLE,UpdateManager.this,UPDATE_CODE);
                } catch (IntentSender.SendIntentException e) {

                    Toast.makeText(UpdateManager.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);

                }

            }

        }).addOnFailureListener(e -> {
            Log.d("onFailure: ",e.getMessage());
        });

    }

    InstallStateUpdatedListener listener = installState -> {

        if (installState.installStatus() == InstallStatus.DOWNLOADED){
            updateSnack();
        }

    };

    private void updateSnack() {

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Update ready to install!",Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Restart", v -> updateManager.completeUpdate());

        snackbar.setTextColor(Color.WHITE);
        snackbar.show();

    }

    protected void onStop() {
        if (updateManager != null)
            updateManager.unregisterListener(listener);

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_CODE && requestCode != RESULT_OK){

            Toast.makeText(UpdateManager.this, "Cancel", Toast.LENGTH_SHORT).show();

        }
    }
}