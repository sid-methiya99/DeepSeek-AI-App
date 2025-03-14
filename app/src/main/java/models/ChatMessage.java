package models;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("chatSession")
    private String chatSession;
    
    @SerializedName("sender")
    private String sender; // User or "Gemini"
    
    @SerializedName("content")
    private String content; // The actual message
    
    @SerializedName("timestamp")
    private Date timestamp;
    
    @SerializedName("isUserMessage")
    private boolean isUserMessage; // True if sent by the user, false if Gemini

    public ChatMessage() {

    }
    public ChatMessage(String sender, String content, Date timestamp, boolean isUserMessage) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.isUserMessage = isUserMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatSession() {
        return chatSession;
    }

    public void setChatSession(String chatSession) {
        this.chatSession = chatSession;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }

    public void setUserMessage(boolean userMessage) {
        isUserMessage = userMessage;
    }
}
