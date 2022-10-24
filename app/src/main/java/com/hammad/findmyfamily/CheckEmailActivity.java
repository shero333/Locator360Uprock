package com.hammad.findmyfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.hammad.findmyfamily.StartScreen.StartScreenActivity;
import com.hammad.findmyfamily.databinding.ActivityCheckEmailBinding;

public class CheckEmailActivity extends AppCompatActivity {

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

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(Intent.createChooser(intent,"EMAIL"));
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

                startActivity(new Intent(CheckEmailActivity.this,ResetPasswordEmailActivity.class));
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