package models;

public class ChatSessionStartResponse {
    private String chatSessionId;
    public ChatSessionStartResponse() {

    }

    public ChatSessionStartResponse(String chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(String chatSessionId) {
        this.chatSessionId = chatSessionId;
    }
}
