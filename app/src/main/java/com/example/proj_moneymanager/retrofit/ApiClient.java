package com.example.proj_moneymanager.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
<<<<<<< HEAD
    private static final String BASE_URL ="http://172.16.0.221/money_management/";
=======
    private static final String BASE_URL ="http://10.0.138.95/money_management/";
>>>>>>> 00b01dac1cdda0ab600031230602168292f3a0c5
    private static Retrofit retrofit =null;

    public static Retrofit getApiClient() {
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
