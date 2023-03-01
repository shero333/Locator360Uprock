package com.kl360.findmyfamilyandfriends.ResetPassword.ByEmail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kl360.findmyfamilyandfriends.R;
import com.kl360.findmyfamilyandfriends.StartScreen.StartScreenActivity;
import com.kl360.findmyfamilyandfriends.databinding.ActivityCheckEmailBinding;

import java.util.ArrayList;
import java.util.List;

public class CheckEmailActivity extends AppCompatActivity {

    private static final String TAG = "CHE_EM_ACT";
    ActivityCheckEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityCheckEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //open mail
        binding.btnOpenEmailApp.setOnClickListener(v -> openMail());

        //skip text view
        binding.txtSkip.setOnClickListener(v -> skip());

        //footer text hyper link
        txtFooterHyperLink();
    }

    private void openMail() {

        try {

            Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
            PackageManager pm = getPackageManager();

            List<ResolveInfo> resInfo = pm.queryIntentActivities(emailIntent, 0);
            if (resInfo.size() > 0) {
                ResolveInfo ri = resInfo.get(0);
                // First create an intent with only the package name of the first registered email app
                // and build a picked based on it
                Intent intentChooser = pm.getLaunchIntentForPackage(ri.activityInfo.packageName);
                Intent openInChooser = Intent.createChooser(intentChooser, "Choose Email");

                // Then create a list of LabeledIntent for the rest of the registered email apps
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 1; i < resInfo.size(); i++) {
                    // Extract the label and repackage it in a LabeledIntent
                    ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    Intent intent = pm.getLaunchIntentForPackage(packageName);
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }

                LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
                // Add the rest of the email apps to the picker selection
                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
            }

        }
        catch (ActivityNotFoundException e) {
            Log.i(TAG, "act not found: "+e.getMessage());
            Toast.makeText(this, "No Email Application Found on device.", Toast.LENGTH_LONG).show();
        }
    }

    private void skip() {

        startActivity(new Intent(this, StartScreenActivity.class));
        finish();
    }

    private void txtFooterHyperLink() {

        SpannableString spannableString=new SpannableString(getString(R.string.txt_footer_));

        //color span for hyperlink
        ForegroundColorSpan fcsTryAnotherEmail=new ForegroundColorSpan(getColor(R.color.orange));

        ClickableSpan clickableSpanTryAnotherEmail=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

                //navigate to the previous activity

                startActivity(new Intent(CheckEmailActivity.this, ResetPasswordEmailActivity.class));
                finish();
            }
        };

        //for making it clickable
        spannableString.setSpan(clickableSpanTryAnotherEmail,49,75, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //for changing the text color to orange
        spannableString.setSpan(fcsTryAnotherEmail,49,75,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.txtFooter.setText(spannableString);
        binding.txtFooter.setMovementMethod(LinkMovementMethod.getInstance());

    }
}