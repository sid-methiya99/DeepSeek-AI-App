package models;

import java.util.Date;

public class ChatMessage {
    private String sender; // User or "Gemini"
    private String content; // The actual message
    private Date timestamp;
    private boolean isUserMessage; // True if sent by the user, false if Gemini

    public ChatMessage() {

    }
    public ChatMessage(String sender, String content, Date timestamp, boolean isUserMessage) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.isUserMessage = isUserMessage;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
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
