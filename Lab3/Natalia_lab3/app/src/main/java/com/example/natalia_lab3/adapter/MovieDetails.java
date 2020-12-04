package com.example.natalia_lab3.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.natalia_lab3.R;
import com.example.natalia_lab3.model.Movie;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MovieDetails extends DialogFragment {
    Movie movie;
    String Internet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = this.getArguments();
            Gson gson = new Gson();
            movie = gson.fromJson(bundle.getString("Movie"), Movie.class);
            Internet = bundle.getString("Internet");
        } else {
            Gson gson = new Gson();
            movie = gson.fromJson(savedInstanceState.getString("Movie"), Movie.class);
            Internet = savedInstanceState.getString("Internet");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        outState.putString("Movie",gson.toJson(movie));
        outState.putString("Internet",Internet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.movie_details, container, false);

        ImageView cover = v.findViewById(R.id.detail_movie_cover);
        TextView title = v.findViewById(R.id.detail_movie_title);
        TextView date = v.findViewById(R.id.detail_movie_date);
        TextView rate = v.findViewById(R.id.detail_movie_rate);
        TextView desc = v.findViewById(R.id.detail_movie_desc);

        title.setText(movie.getTitle());
        date.setText(movie.getReleaseDate());
        rate.setText(movie.getPopularity().toString());
        desc.setText(movie.getOverview());

        if(Internet == "1")
            Picasso.get().load(movie.getPosterPath()).into(cover);
        else
            cover.setImageResource(R.drawable.photo);
        return v;
    }

}
