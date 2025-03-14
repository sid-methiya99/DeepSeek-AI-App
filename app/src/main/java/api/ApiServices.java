package api;

import models.ChatMessage;
import models.ChatMessageResponse;
import models.ChatSessionStartResponse;
import models.LoginRequest;
import models.LoginResponse;
import models.SendChatMessageRequest;
import models.SignUpRequest;
import models.CloseChatSessionRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiServices {
    @POST("api/v1/user/signup")
    Call<LoginResponse> signUp(@Body SignUpRequest request);

    @POST("api/v1/user/signin")
    Call<LoginResponse> signIn(@Body LoginRequest request);

    @POST("api/v1/chat/start")
    Call<ChatSessionStartResponse> startChatSession();

    @POST("api/v1/chat/send")
    Call<ChatMessageResponse> sendChatMessage(@Body SendChatMessageRequest sendChatMessageRequest);

    @GET("api/v1/chat/history/{chatSessionId}")
    Call<List<ChatMessage>> getChatHistory(@Path("chatSessionId") String chatSessionId);

    @POST("api/v1/chat/close")
    Call<Void> closeChatSession(@Body CloseChatSessionRequest request);
}
