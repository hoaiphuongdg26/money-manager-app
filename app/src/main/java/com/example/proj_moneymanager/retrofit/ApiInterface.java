package com.example.proj_moneymanager.retrofit;

import com.example.proj_moneymanager.models.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login.php")
    Call<ApiResponse> performUserLogIn(@Field("UserName") String userName,@Field("Password") String password);
    @FormUrlEncoded
    @POST("signup.php")
    Call<ApiResponse> performUserSignUp(@Field("UserName") String userName,@Field("Password") String password,@Field("Name") String name);
}
