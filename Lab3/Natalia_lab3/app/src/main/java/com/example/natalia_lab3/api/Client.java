package com.example.natalia_lab3.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Client mInstance;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private Retrofit mRetrofit;

    private Client() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Client getInstance() {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    public Service getJSONApi() {
        return mRetrofit.create(Service.class);
    }
}