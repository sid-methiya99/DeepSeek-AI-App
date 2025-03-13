package api;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.TokenManager;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit;

    public static ApiServices getApiService(Context context){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response details

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // Add logging interceptor
            httpClient.addInterceptor(new TokenManager(context));

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
                    .client(httpClient.build()) // Set the OkHttpClient
                    .build();
        return retrofit.create(ApiServices.class);
    }
}
