package models;

public class CloseChatSessionRequest {
    private String chatSessionId;

    public CloseChatSessionRequest(String chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(String chatSessionId) {
        this.chatSessionId = chatSessionId;
    }
} 