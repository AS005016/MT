package com.example.natalia_lab3.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.natalia_lab3.MainActivity;
import com.example.natalia_lab3.R;
import com.example.natalia_lab3.api.Client;
import com.example.natalia_lab3.db.AppDatabase;
import com.example.natalia_lab3.db.MovieDao;
import com.example.natalia_lab3.model.Movie;
import com.example.natalia_lab3.model.Movies;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieList extends ListFragment {
    Movie[] movies;
    MyArrayAdapter adapter;
    String api_key = "cc6a9c73ea8fd63aed385051b8034ef5";
    MovieDetails movieDetails;
    AppDatabase db;
    boolean noInternet;
    private static String FRAGMENT_INSTANCE_NAME = "details_fragment";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null) {
            setRetainInstance(true);
            db = MainActivity.getInstance().getDatabase();
            MovieDao movieDao = db.movieDao();
            // очистка сохраненной БД
//        for(Movie m : movieDao.getAll())
//            movieDao.delete(m);
            Client.getInstance()
                    .getJSONApi()
                    .getPopularMovies(api_key, 4)
                    .enqueue(new Callback<Movies>() {
                        @Override
                        public void onResponse(Call<Movies> call, Response<Movies> response) {
                            // если запрос на сервер завершился успешно - показать загруженный список
                            List<Movie> _movies = response.body().getMovies();
                            movies = new Movie[_movies.size()];
                            _movies.toArray(movies);
                            adapter = new MyArrayAdapter(getActivity(), R.layout.movie_in_list, movies);
                            setListAdapter(adapter);
                            for (Movie m : _movies) {
                                if (movieDao.getCurrent(m.getId()).size() == 0)
                                    movieDao.insertAll(m);
                            }
                            noInternet = false;
                        }

                        @Override
                        public void onFailure(Call<Movies> call, Throwable t) {
                            // если запрос на сервер завершился неудачно - попытаться загрузить данные из БД
                            Log.d("Adapter", "Error");
                            List<Movie> _movies = movieDao.getAll();
                            //если есть сохраненные элементы - вывести их
                            if (_movies.size() > 0) {
                                movies = new Movie[_movies.size()];
                                _movies.toArray(movies);
                                adapter = new MyArrayAdapter(getActivity(), R.layout.movie_in_list, movies);
                                setListAdapter(adapter);
                                noInternet = true;
                            } else { // если сохраненных элементов нет - вывести сообщение
                                Movie movie = new Movie("", false, "",
                                        "", -1, "", "", "Error loading data. No stored movies",
                                        "", 0.0, -1, false, 0.0);
                                movies = new Movie[1];
                                movies[0] = movie;
                                adapter = new MyArrayAdapter(getActivity(), R.layout.movie_in_list, movies);
                                setListAdapter(adapter);
                            }
                        }
                    });
        }
        else {
            // если есть сохраненное состояние - загрузить из него список объектов
            Gson gson = new Gson();
            movies = gson.fromJson(savedInstanceState.getString("MoviesList"),Movie[].class);
            adapter = new MyArrayAdapter(getActivity(), R.layout.movie_in_list, movies);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // сохраняем список при изменении ориентации экрана и не только
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        String json = gson.toJson(movies);
        outState.putString("MoviesList", json);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(movies[position].getId() == -1)
            return;
        movieDetails = new MovieDetails();
        Gson gson = new Gson();
        String json = gson.toJson(movies[position]);
        Bundle b = new Bundle();
        b.putString("Movie", json);
        b.putString("Internet", noInternet?"0":"1");
        movieDetails.setArguments(b);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.listFragment, movieDetails, FRAGMENT_INSTANCE_NAME)
                .addToBackStack(null)
                .commit();
    }

    public class MyArrayAdapter extends ArrayAdapter {
        private Context context;
        int itemListResourceId;

        public MyArrayAdapter(Context _context, int itemListResourceId, Movie[] movies){
            super(_context, itemListResourceId, movies);
            this.itemListResourceId = itemListResourceId;
            context = _context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(itemListResourceId, parent,
                    false);


            TextView title = row.findViewById(R.id.title);
            TextView rating = row.findViewById(R.id.userRating);
            ImageView thumbnail = row.findViewById(R.id.thumbnail);

            title.setText(movies[position].getTitle());
            rating.setText(movies[position].getVoteCount().toString());

            if(!noInternet)
                Picasso.get().load(movies[position].getPosterPath()).into(thumbnail);
            else
                thumbnail.setImageResource(R.drawable.photo);
            return row;
        }
    }
}
