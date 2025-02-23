package com.example.deepseekaiconventionalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signUpEmail, signUpPassword;
    private Button signUpButton;
    private TextView loginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signUpEmail = findViewById(R.id.signup_email);
        signUpPassword = findViewById(R.id.signup_password);
        signUpButton = findViewById(R.id.signup_button);
        loginRedirect = findViewById(R.id.loginRedirectText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signUpEmail.getText().toString().trim();
                String password = signUpPassword.getText().toString().trim();

                if(user.isEmpty()){
                    signUpEmail.setError("Email cannot be found");
                }
                if(password.isEmpty()){
                    signUpPassword.setError("Password cannot be found");
                }
            }
        });
    }
}