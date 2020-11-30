package com.example.practicaandroid.ui.Favorites;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.db.DbHelper;
import com.example.practicaandroid.db.adapaters.MovieDetailAdapter;
import com.example.practicaandroid.R;
import com.example.practicaandroid.db.adapaters.SerieDetailAdapter;
import com.example.practicaandroid.db.models.MovieDetail;
import com.example.practicaandroid.db.models.TVShowDetail;
import com.example.practicaandroid.retrofit.MovieService;
import com.example.practicaandroid.retrofit.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesFragment extends Fragment {

    private FavouritesViewModel favouritesViewModel;
    RecyclerView rv_recommended_movies;
    RecyclerView rv_recommended_series;
    MovieDetailAdapter movieAdapter;
    SerieDetailAdapter serieAdapter;

    ConstraintLayout blackLayer;

    float id = 0;

    List<MovieDetail> favouriteMovies = new ArrayList<>();
    List<TVShowDetail> favouriteSeries = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouritesViewModel =
               ViewModelProviders.of(this).get(FavouritesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);



        rv_recommended_movies = (RecyclerView) root.findViewById(R.id.rv_recommend_movies);
        rv_recommended_movies.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_recommended_movies.setHasFixedSize(true);
        rv_recommended_movies.addItemDecoration(new DividerItemDecoration(rv_recommended_movies.getContext(), DividerItemDecoration.VERTICAL));
        rv_recommended_movies.setItemAnimator(new DefaultItemAnimator());

        rv_recommended_series = (RecyclerView) root.findViewById(R.id.rv_recommend_series);
        rv_recommended_series.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_recommended_series.setHasFixedSize(true);
        rv_recommended_series.addItemDecoration(new DividerItemDecoration(rv_recommended_movies.getContext(), DividerItemDecoration.VERTICAL));
        rv_recommended_series.setItemAnimator(new DefaultItemAnimator());

        movieAdapter = new MovieDetailAdapter(this.getContext());
        serieAdapter = new SerieDetailAdapter(this.getContext());

        rv_recommended_movies.setAdapter(movieAdapter);
        rv_recommended_series.setAdapter(serieAdapter);


        blackLayer = (ConstraintLayout) root.findViewById(R.id.blackLayer);


        DbHelper dbHelper = new DbHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db != null) {
            String[] columns = {"_id", "idFav"};
            Cursor favouriteMovies = db.query("favIds", columns, null, null, null, null, null);

            while (favouriteMovies.moveToNext()) {
                long itemId = favouriteMovies.getLong(
                        favouriteMovies.getColumnIndexOrThrow("idFav"));
                id = itemId;
                loadSearch();
            }

            Cursor favouriteSeries = db.query("series", columns, null, null, null, null, null );

            while(favouriteSeries.moveToNext()) {
                long itemId = favouriteSeries.getLong(
                        favouriteSeries.getColumnIndexOrThrow("idFav"));
                id = itemId;
                loadSearchSeries();
            }

            favouriteMovies.close();

        }

        return root;
    }

    public void loadSearch () {

        int idReal = (int) id;

        MovieService movieService = RetrofitInstance.getRetrofitInstance().create(MovieService.class);

        Call<MovieDetail> call = movieService.getMovieByID(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));
        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                switch (response.code()) {

                    case 200:
                        MovieDetail data = response.body();
                        favouriteMovies.add(data);
                        movieAdapter.swap(favouriteMovies);
                        movieAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Toast.makeText(movieAdapter.context, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void loadSearchSeries () {


        int idReal = (int) id;

        MovieService movieService = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<TVShowDetail> call = movieService.getSerieByID(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));
        call.enqueue(new Callback<TVShowDetail>() {
            @Override
            public void onResponse(Call<TVShowDetail> call, Response<TVShowDetail> response) {
                switch (response.code()) {

                    case 200:
                        TVShowDetail data = response.body();
                        favouriteSeries.add(data);
                        serieAdapter.swap(favouriteSeries);
                        serieAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<TVShowDetail> call, Throwable t) {
                Toast.makeText(serieAdapter.context, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


}