package com.example.deepseekaiconventionalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import models.ChatMessage;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private Context context;
    private List<ChatMessage> messages;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMessage message = getItem(position);
        
        // Always inflate new view to ensure correct layout is used
        convertView = LayoutInflater.from(context).inflate(
            message.isUserMessage() ? R.layout.user_message_item : R.layout.bot_message_item,
            parent,
            false
        );

        TextView messageText = convertView.findViewById(R.id.message_text);
        messageText.setText(message.getContent());

        return convertView;
    }
} 