package com.hammad.findmyfamily.SignUp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityEmailSignUpBinding;

import java.util.ArrayList;
import java.util.List;

public class EmailSignUpActivity extends AppCompatActivity {

    private static final String TAG = "EMAIL_SIGN_UP";

    private ActivityEmailSignUpBinding binding;

    List<String> registeredEmailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityEmailSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //get data
        getRegisteredUsersEmail();

        //TextWatcher
        binding.edtEmailSignUp.addTextChangedListener(emailTextWatcher);

        //button click listener
        binding.btnContEmailSignUp.setOnClickListener(v -> buttonClickListener());

    }

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            if (Commons.validateEmailAddress(s)) {

                binding.btnContEmailSignUp.setEnabled(true);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.white_rounded_button);
            } else if (!Commons.validateEmailAddress(s)) {
                binding.btnContEmailSignUp.setEnabled(false);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void buttonClickListener() {

        for (int i = 0; i < registeredEmailList.size(); i++) {

            if (registeredEmailList.get(i).equals(binding.edtEmailSignUp.getText().toString())) {
                Toast.makeText(this, getString(R.string.email_already_in_use), Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }

    //function for checking if entered email already exists or not
    private void getRegisteredUsersEmail() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        registeredEmailList.add(doc.getId());
                    }
                })
                .addOnFailureListener(e -> Log.i(TAG, "onFailure: " + e.getMessage()));

    }
}