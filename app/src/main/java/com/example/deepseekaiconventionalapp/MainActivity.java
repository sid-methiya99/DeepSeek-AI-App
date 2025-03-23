package com.example.deepseekaiconventionalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.ApiServices;
import api.RetrofitClient;
import models.ChatMessage;
import models.ChatMessageResponse;
import models.ChatSession;
import models.ChatSessionStartResponse;
import models.CloseChatSessionRequest;
import models.SendChatMessageRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ListView messagesListView;
    private EditText messageInput;
    private ImageButton sendButton;
    private List<ChatMessage> messagesList;
    private ChatMessageAdapter adapter;
    private SharedPreferences sharedPreferences;
    private String sessionId;
    private boolean isTypingIndicatorShown = false;
    private String savedSessionId;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private ImageButton newChatButton;
    private TextView chatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", "");
        
        if (token.isEmpty()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize message list and adapter
        messagesList = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, messagesList);

        // Initialize views and setup
        initializeViews();

        // Get saved session ID
        savedSessionId = sharedPreferences.getString("last_session_id", "");
        
        if (!savedSessionId.isEmpty()) {
            // Use existing session and fetch history
            sessionId = savedSessionId;
            fetchChatHistory();
        } else {
            // Start new chat session
            startChatSession();
        }
    }

    private void initializeViews() {
        messagesListView = findViewById(R.id.messages_list);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menuButton = findViewById(R.id.menu_button);
        newChatButton = findViewById(R.id.new_chat_button);
        chatTitle = findViewById(R.id.chat_title);

        // Set adapter
        messagesListView.setAdapter(adapter);

        // Set click listeners
        sendButton.setOnClickListener(v -> sendMessage());
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        newChatButton.setOnClickListener(v -> startNewChat());

        // Make sure list scrolls to bottom when keyboard appears
        messagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messagesListView.setStackFromBottom(true);

        // Setup navigation drawer
        setupNavigationDrawer();
    }

    private void setupNavigationDrawer() {
        // Remove any existing header views
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        
        // Inflate the navigation header layout
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        
        // Set user info in header
        TextView userNameView = headerView.findViewById(R.id.nav_header_name);
        TextView userEmailView = headerView.findViewById(R.id.nav_header_email);
        
        String username = sharedPreferences.getString("username", "User");
        String email = sharedPreferences.getString("user_email", "");
        
        userNameView.setText(username);
        userEmailView.setText(email);

        // Load chat history
        loadChatHistory();

        // Setup menu items
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                drawerLayout.closeDrawer(GravityCompat.START);
                logout();
                return true;
            } else if (item.getGroupId() == R.id.chat_history_group) {
                String sessionId = item.getIntent().getStringExtra("session_id");
                loadChatSession(sessionId);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });
    }

    private void startChatSession() {
        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<ChatSessionStartResponse> call = apiServices.startChatSession();

        call.enqueue(new Callback<ChatSessionStartResponse>() {
            @Override
            public void onResponse(Call<ChatSessionStartResponse> call, Response<ChatSessionStartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionId = response.body().getChatSessionId();
                    // Save the session ID to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("last_session_id", sessionId);
                    editor.apply();
                    
                    android.util.Log.d("MainActivity", "New session ID: " + sessionId);
                    
                    // After getting sessionId, fetch chat history
                    fetchChatHistory();
                    Toast.makeText(MainActivity.this, "Chat session started successfully", Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 401) {
                        handleUnauthorized();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to start chat session", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatSessionStartResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChatHistory() {
        if (sessionId == null || sessionId.isEmpty()) return;

        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<List<ChatMessage>> call = apiServices.getChatHistory(sessionId);

        call.enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    List<ChatMessage> history = response.body();
                    if (history != null && !history.isEmpty()) {
                        // Clear existing messages
                        messagesList.clear();
                        
                        // Add messages from history
                        for (ChatMessage message : history) {
                            // Create new ChatMessage objects to ensure proper formatting
                            messagesList.add(new ChatMessage(
                                message.isUserMessage() ? "user" : "bot",
                                message.getContent(),
                                message.getTimestamp(),
                                message.isUserMessage()
                            ));
                        }
                        
                        // Update UI
                        adapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                } else if (response.code() == 401) {
                    handleUnauthorized();
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching history: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTypingIndicator() {
        if (!isTypingIndicatorShown) {
            messagesList.add(new ChatMessage("bot", "", new Date(), false));
            adapter.notifyDataSetChanged();
            scrollToBottom();
            isTypingIndicatorShown = true;
        }
    }

    private void hideTypingIndicator() {
        if (isTypingIndicatorShown) {
            messagesList.remove(messagesList.size() - 1);
            adapter.notifyDataSetChanged();
            isTypingIndicatorShown = false;
        }
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        messageInput.setText("");

        ChatMessage userMessage = new ChatMessage("user", messageText, new Date(), true);
        messagesList.add(userMessage);
        adapter.notifyDataSetChanged();
        scrollToBottom();

        // Show typing indicator
        showTypingIndicator();

        SendChatMessageRequest request = new SendChatMessageRequest(sessionId, messageText);
        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<ChatMessageResponse> call = apiServices.sendChatMessage(request);

        call.enqueue(new Callback<ChatMessageResponse>() {
            @Override
            public void onResponse(Call<ChatMessageResponse> call, Response<ChatMessageResponse> response) {
                hideTypingIndicator();

                if (response.isSuccessful() && response.body() != null) {
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
                    if (response.code() == 401) {
                        handleUnauthorized();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatMessageResponse> call, Throwable t) {
                hideTypingIndicator();
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        messagesListView.post(() -> {
            messagesListView.setSelection(adapter.getCount() - 1);
        });
    }

    // Add method to handle token expiration
    private void handleUnauthorized() {
        // Clear the token
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt_token");
        editor.apply();

        // Redirect to login
        Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void logout() {
        // Clear all data including session ID
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login screen
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("fromLogout", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadChatHistory() {
        ApiServices apiServices = RetrofitClient.getApiService(this);
        Call<List<ChatSession>> call = apiServices.getChatSessions();

        call.enqueue(new Callback<List<ChatSession>>() {
            @Override
            public void onResponse(Call<List<ChatSession>> call, Response<List<ChatSession>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Menu menu = navigationView.getMenu();
                    menu.removeGroup(R.id.chat_history_group);  // Clear existing items

                    for (ChatSession session : response.body()) {
                        MenuItem item = menu.add(R.id.chat_history_group, Menu.NONE, 0, session.getName());
                        item.setIcon(R.drawable.ic_chat);
                        
                        // Store session ID in the menu item
                        Intent intent = new Intent();
                        intent.putExtra("session_id", session.getId());
                        item.setIntent(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ChatSession>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load chat history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatSession(String sessionId) {
        // Clear current chat
        messagesList.clear();
        adapter.notifyDataSetChanged();
        
        // Set current session ID
        this.sessionId = sessionId;
        
        // Load messages for this session
        fetchChatHistory();
    }

    private void startNewChat() {
        if (messagesList.isEmpty()) {
            Toast.makeText(this, "Already in new chat", Toast.LENGTH_SHORT).show();
            return;
        }

        // Close current session
        if (sessionId != null && !sessionId.isEmpty()) {
            CloseChatSessionRequest request = new CloseChatSessionRequest(sessionId);
            ApiServices apiServices = RetrofitClient.getApiService(this);
            Call<ChatSession> call = apiServices.closeChatSession(request);

            call.enqueue(new Callback<ChatSession>() {
                @Override
                public void onResponse(Call<ChatSession> call, Response<ChatSession> response) {
                    if (response.isSuccessful()) {
                        // Clear messages and start new session
                        messagesList.clear();
                        adapter.notifyDataSetChanged();
                        startChatSession();
                        
                        // Refresh chat history
                        loadChatHistory();
                    }
                }

                @Override
                public void onFailure(Call<ChatSession> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed to close chat session", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
