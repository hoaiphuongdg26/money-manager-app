package com.example.proj_moneymanager.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL ="http://172.16.2.76/money_management/";
    private static Retrofit retrofit =null;

    public static Retrofit getApiClient() {
        if(retrofit==null)
        {
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(new OkHttpClient()).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return retrofit;
    }
}
