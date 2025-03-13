package com.example.deepseekaiconventionalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.ApiServices;
import api.RetrofitClient;
import models.ChatMessage;
import models.ChatMessageResponse;
import models.ChatSessionStartResponse;
import models.SendChatMessageRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ListView messagesListView;
    private EditText messageInput;
    private Button sendButton;
    private List<ChatMessage> messagesList;
    private ChatMessageAdapter adapter;
    private SharedPreferences sharedPreferences;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        messagesListView = findViewById(R.id.messages_list);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Initialize message list and adapter
        messagesList = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, messagesList);
        messagesListView.setAdapter(adapter);

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        
        // Start chat session
        startChatSession();

        // Set click listener for send button
        sendButton.setOnClickListener(v -> sendMessage());

        // Make sure list scrolls to bottom when keyboard appears
        messagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messagesListView.setStackFromBottom(true);
    }

    private void startChatSession() {
        String token = sharedPreferences.getString("jwt_token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<ChatSessionStartResponse> call = apiServices.startChatSession("Bearer " + token);

        call.enqueue(new Callback<ChatSessionStartResponse>() {
            @Override
            public void onResponse(Call<ChatSessionStartResponse> call, Response<ChatSessionStartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionId = response.body().getChatSessionId();
                    Toast.makeText(MainActivity.this, "Chat session started successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to start chat session", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatSessionStartResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        // Clear input field
        messageInput.setText("");

        // Add user message to list
        ChatMessage userMessage = new ChatMessage("user", messageText, new Date(), true);
        messagesList.add(userMessage);
        adapter.notifyDataSetChanged();
        scrollToBottom();

        // Get token from SharedPreferences
        String token = sharedPreferences.getString("jwt_token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send message to server
        SendChatMessageRequest request = new SendChatMessageRequest(sessionId, messageText);
        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<ChatMessageResponse> call = apiServices.sendChatMessage("Bearer " + token, request);

        call.enqueue(new Callback<ChatMessageResponse>() {
            @Override
            public void onResponse(Call<ChatMessageResponse> call, Response<ChatMessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Add bot response to list
                    ChatMessage botMessage = new ChatMessage(
                        "bot",
                        response.body().getReply(),
                        new Date(),
                        false
                    );
                    messagesList.add(botMessage);
                    adapter.notifyDataSetChanged();
                    scrollToBottom();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatMessageResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        messagesListView.post(() -> {
            messagesListView.setSelection(adapter.getCount() - 1);
        });
    }
}
