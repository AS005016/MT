package com.example.natalia_lab3.api;

import com.example.natalia_lab3.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("movie/popular")
    Call<Movies> getPopularMovies(@Query("api_key")String apiKey, @Query("page")Integer page);
}
