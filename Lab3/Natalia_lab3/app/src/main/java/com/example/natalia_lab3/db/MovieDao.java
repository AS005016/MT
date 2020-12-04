package com.example.natalia_lab3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.natalia_lab3.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    @Query("SELECT * FROM movies WHERE id=:id")
    List<Movie> getCurrent(Integer id);

    @Insert
    void insertAll(Movie... movies);

    @Delete
    void delete(Movie movie);
}
