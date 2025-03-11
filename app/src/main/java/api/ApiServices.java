package api;

import models.LoginRequest;
import models.LoginResponse;
import models.SignUpRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServices {
    @POST("api/v1/user/signup")
    Call<LoginResponse> signUp(@Body SignUpRequest request);

    @POST("api/v1/user/signin")
    Call<LoginResponse> signIn(@Body LoginRequest request);
}
