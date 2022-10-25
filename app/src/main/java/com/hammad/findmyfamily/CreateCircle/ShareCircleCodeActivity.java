package com.hammad.findmyfamily.CreateCircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.OneTimeScreens.AddProfilePictureActivity;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityShareCircleCodeBinding;

public class ShareCircleCodeActivity extends AppCompatActivity {

    private ActivityShareCircleCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityShareCircleCodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //getting the randomly generated circle code
        setCircleCode(Commons.getRandomGeneratedCircleCode());

        //share code click listener
        binding.btnShareCode.setOnClickListener(v -> shareCircleCode());

        //text close click listener
        binding.txtClose.setOnClickListener(v -> startActivity(new Intent(this, AddProfilePictureActivity.class)));
    }

    //setting the code to textviews
    private void setCircleCode(String code) {
        binding.textViewCircle1.setText(String.valueOf(code.charAt(0)));
        binding.textViewCircle2.setText(String.valueOf(code.charAt(1)));
        binding.textViewCircle3.setText(String.valueOf(code.charAt(2)));
        binding.textViewCircle4.setText(String.valueOf(code.charAt(3)));
        binding.textViewCircle5.setText(String.valueOf(code.charAt(4)));
        binding.textViewCircle6.setText(String.valueOf(code.charAt(5)));

        //saving the circle code in preference as well
        SharedPreference.setCircleInviteCode(code);
    }

    //this function will have to implement the Firebase dynamic link
    private void shareCircleCode() {
        Toast.makeText(this, "Share Code", Toast.LENGTH_SHORT).show();
    }

}