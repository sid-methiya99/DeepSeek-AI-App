package com.example.deepseekaiconventionalapp;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    private static final int VIEW_TYPE_TYPING = 2;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getViewTypeCount() {
        return 3; // User, Bot, and Typing indicator
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.getContent().isEmpty()) {
            return VIEW_TYPE_TYPING;
        }
        return message.isUserMessage() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        ChatMessage message = getItem(position);

        if (viewType == VIEW_TYPE_TYPING) {
            View view = LayoutInflater.from(context).inflate(R.layout.typing_indicator, parent, false);
            
            // Get the dots
            ImageView dot1 = view.findViewById(R.id.dot1);
            ImageView dot2 = view.findViewById(R.id.dot2);
            ImageView dot3 = view.findViewById(R.id.dot3);

            // Create animations
            dot1.animate().alpha(1f).setDuration(500).setStartDelay(0)
                .withEndAction(() -> dot1.animate().alpha(0.4f).setDuration(500));
            dot2.animate().alpha(1f).setDuration(500).setStartDelay(200)
                .withEndAction(() -> dot2.animate().alpha(0.4f).setDuration(500));
            dot3.animate().alpha(1f).setDuration(500).setStartDelay(400)
                .withEndAction(() -> dot3.animate().alpha(0.4f).setDuration(500));

            // Repeat animations
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (view.getParent() != null) {
                        dot1.animate().alpha(1f).setDuration(500).setStartDelay(0)
                            .withEndAction(() -> dot1.animate().alpha(0.4f).setDuration(500));
                        dot2.animate().alpha(1f).setDuration(500).setStartDelay(200)
                            .withEndAction(() -> dot2.animate().alpha(0.4f).setDuration(500));
                        dot3.animate().alpha(1f).setDuration(500).setStartDelay(400)
                            .withEndAction(() -> dot3.animate().alpha(0.4f).setDuration(500));
                        view.postDelayed(this, 1500);
                    }
                }
            });
            
            return view;
        }

        ViewHolder holder;
        if (convertView == null || convertView.findViewById(R.id.dot1) != null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                viewType == VIEW_TYPE_USER ? R.layout.user_message_item : R.layout.bot_message_item,
                parent,
                false
            );
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