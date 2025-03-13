package com.example.deepseekaiconventionalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import api.ApiServices;
import api.RetrofitClient;
import models.LoginRequest;
import models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private TextView signUpRedirectText;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        signUpRedirectText = findViewById(R.id.signUpRedirectText);

        loginButton.setOnClickListener(v -> signIn());

        signUpRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void signIn(){

        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<LoginResponse> call = apiServices.signIn(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {

                        String token = loginResponse.getToken();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("jwt_token", token);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Replace MainActivity.class
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sign in successful, but token is null!", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "AuthResponse body is null");
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Sign in failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("LoginActivity", "Sign in failed: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("LoginActivity", "Error getting error body: " + e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Sign in failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Sign in failed: " + t.getMessage(), t);
            }
        });
    }
}
