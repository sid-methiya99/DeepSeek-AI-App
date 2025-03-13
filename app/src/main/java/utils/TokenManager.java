package utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenManager implements Interceptor {
    private Context context;

    public TokenManager(Context context){
       this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // Only add token for chat endpoints
        if (path.contains("/chat/")) {
            // Get token from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("jwt_token", "");

            // Add token to request header
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }

        // For non-chat endpoints, proceed with original request
        return chain.proceed(originalRequest);
    }
}
