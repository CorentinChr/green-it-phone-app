package com.uphf.sae6_app.retrofit;

import com.uphf.sae6_app.retrofit.dto.UserRequest;
import com.uphf.sae6_app.retrofit.dto.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {

    @POST("users")
    Call<UserResponse> upsertUser(@Body UserRequest user);

    @GET("users/{id}")
    Call<UserResponse> getUser(@Path("id") String id);

    // Optionnel : endpoint avec header auth
    // @POST("users")
    // Call<UserResponse> upsertUserAuth(@Header("Authorization") String bearerToken, @Body UserRequest user);
}

