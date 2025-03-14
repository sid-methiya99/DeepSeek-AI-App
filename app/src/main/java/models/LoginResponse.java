package models;

public class LoginResponse {
    private String token;
    private String email;
    private String username;
    private String recentChatSessionId;

    public LoginResponse() {
    }

    public LoginResponse(String token, String email, String username, String recentChatSessionId) {
        this.token = token;
        this.email = email;
        this.username = username;
        this.recentChatSessionId = recentChatSessionId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRecentChatSessionId() {
        return recentChatSessionId;
    }
}
