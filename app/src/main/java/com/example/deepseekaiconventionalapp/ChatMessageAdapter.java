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
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // We have two types of views: user and bot messages
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        return message.isUserMessage() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        ChatMessage message = getItem(position);
        int viewType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            if (viewType == VIEW_TYPE_USER) {
                convertView = LayoutInflater.from(context).inflate(R.layout.user_message_item, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.bot_message_item, parent, false);
            }
            holder.messageText = convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.messageText.setText(message.getContent());
        return convertView;
    }

    private static class ViewHolder {
        TextView messageText;
    }
} 