package com.example.deepseekaiconventionalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import api.ApiServices;
import api.RetrofitClient;
import models.LoginResponse;
import models.SignUpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText signupUsername, signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginLink;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginLink = findViewById(R.id.login_link);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        signupButton.setOnClickListener(v -> signUp());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void signUp(){
        String username = signupUsername.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SignUpRequest signUpRequest = new SignUpRequest(email, username, password);
        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<LoginResponse> call = apiServices.signUp(signUpRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    LoginResponse loginResponse = response.body();
                    if(loginResponse != null){
                        Toast.makeText(SignUpActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Signup failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SignUpActivity", "Signup failed: " + t.getMessage(), t);
            }
        });
    }
}