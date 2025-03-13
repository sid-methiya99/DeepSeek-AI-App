package models;

public class SendChatMessageRequest {
    private String chatSessionId;
    private String prompt;

    public SendChatMessageRequest(String chatSessionId,String prompt){
        this.chatSessionId = chatSessionId;
        this.prompt = prompt;
    }

    public String getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(String chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
