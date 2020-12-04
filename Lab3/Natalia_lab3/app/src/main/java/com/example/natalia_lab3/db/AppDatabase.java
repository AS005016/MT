package com.example.natalia_lab3.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.natalia_lab3.model.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
}
