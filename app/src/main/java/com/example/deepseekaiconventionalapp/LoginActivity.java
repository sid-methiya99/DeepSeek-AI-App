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
        
        // Check for existing token before setting content view
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        
        // Only clear token if we're coming from logout
        boolean fromLogout = getIntent().getBooleanExtra("fromLogout", false);
        if (fromLogout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        String token = sharedPreferences.getString("jwt_token", "");
        
        // If token exists and we're not coming from logout, redirect to MainActivity
        if (!token.isEmpty() && !fromLogout) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialize views
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
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
                        // Clear any existing data first
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Now set the new credentials
                        editor = sharedPreferences.edit();
                        editor.putString("jwt_token", loginResponse.getToken());
                        editor.putString("user_email", loginResponse.getEmail());
                        editor.putString("username", loginResponse.getUsername());
                        
                        // Save the recent chat session ID if it exists
                        String recentSessionId = loginResponse.getRecentChatSessionId();
                        if (recentSessionId != null && !recentSessionId.isEmpty()) {
                            editor.putString("last_session_id", recentSessionId);
                        }
                        
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        // Parse the error response
                        String errorBody = response.errorBody().string();
                        if (errorBody.contains("Invalid credentials")) {
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign in failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("LoginActivity", "Sign in failed: " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Error getting error body: " + e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Sign in failed: " + t.getMessage(), t);
            }
        });
    }
}
