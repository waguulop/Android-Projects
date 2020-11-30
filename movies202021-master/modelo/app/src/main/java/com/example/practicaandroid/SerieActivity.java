package com.example.practicaandroid;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.practicaandroid.databinding.ActivitySerieBinding;
import com.example.practicaandroid.db.DbHelper;
import com.example.practicaandroid.ui.Videos.Result;
import com.example.practicaandroid.ui.Videos.Videos;
import com.example.practicaandroid.db.adapaters.CastAdapter;
import com.example.practicaandroid.db.adapaters.SerieAdapter;
import com.example.practicaandroid.db.models.CreditsFeed;
import com.example.practicaandroid.db.models.TVShowDetail;
import com.example.practicaandroid.db.models.TVShowFeed;
import com.example.practicaandroid.retrofit.MovieService;
import com.example.practicaandroid.retrofit.RetrofitInstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieActivity extends AppCompatActivity {
    float id;
    CastAdapter serieAdapter;
    SerieAdapter similar;

    List<Result> allVideos = new ArrayList<>();
    Result trailer = new Result();



    ActivitySerieBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivitySerieBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        id = intent.getFloatExtra("id", 0);

        binding.rvRecommendSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommendSeries.setHasFixedSize(true);
        binding.rvRecommendSeries.addItemDecoration(new DividerItemDecoration(binding.rvRecommendSeries.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvRecommendSeries.setItemAnimator(new DefaultItemAnimator());

        binding.rvCasting.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCasting.setHasFixedSize(true);
        binding.rvCasting.addItemDecoration(new DividerItemDecoration(binding.rvRecommendSeries.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvCasting.setItemAnimator(new DefaultItemAnimator());

        serieAdapter = new CastAdapter(this);
        similar = new SerieAdapter(this);
        binding.rvRecommendSeries.setAdapter(serieAdapter);
        binding.rvCasting.setAdapter(similar);

        loadSimilarSeries();
        loadCast();
        loadTrailer();
        loadSearch();
    }

    public void bindSerie(final TVShowDetail serie){

        binding.tvTitle.setText(String.valueOf(serie.getName()));
        binding.sinopsis.setText(String.valueOf(serie.getOverview()));
        binding.numberEpisodes.setText(String.valueOf(serie.getNumber_of_episodes()).substring(0, String.valueOf(serie.getNumber_of_episodes()).length() - 2 ));
        binding.numberSeasons.setText(String.valueOf(serie.getNumber_of_seasons()).substring(0, String.valueOf(serie.getNumber_of_seasons()).length() - 2 ));
        binding.status.setText(String.valueOf(serie.getStatus()));
        if(serie.getPoster_path() != null){
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + serie.getPoster_path()).into(binding.movieImage);
        } else {
            Picasso.get().load(R.mipmap.ic_serie_without_photo).into(binding.movieImage);
        }

        float punctuation = (serie.getVote_average()) / 2;
        binding.ratingBar2.setNumStars(5);
        binding.ratingBar2.setRating(punctuation);
        binding.studio.setText("");
        if(serie.getProductionCompanies() != null && !serie.getProductionCompanies().isEmpty()) {
            for (int i = 0; i < serie.getProductionCompanies().size() - 1; i++) {
                binding.studio.setText(binding.studio.getText() + String.valueOf(serie.getProductionCompanies().get(i)).substring(String.valueOf(serie.getProductionCompanies().get(i)).indexOf("name") + 5, String.valueOf(serie.getProductionCompanies().get(i)).indexOf("origin_country") - 2) + ", ");

            }
            binding.studio.setText(binding.studio.getText() + String.valueOf(serie.getProductionCompanies().get(serie.getProductionCompanies().size() - 1)).substring(String.valueOf(serie.getProductionCompanies().get(serie.getProductionCompanies().size() - 1)).indexOf("name") + 5, String.valueOf(serie.getProductionCompanies().get(serie.getProductionCompanies().size() - 1)).indexOf("origin_country") - 2) + ".");
        }

        final float movieId = serie.getId();

        if(allVideos != null) {
            for (int i = 0; i < allVideos.size(); i++) {
                if (allVideos.get(i).getType().equals("Trailer")) {
                    trailer = allVideos.get(i);
                }
            }
        }

        binding.btWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trailer != null) {
                    Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(SerieActivity.this, getString(R.string.trailer_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.genre.setText("");
        if(serie.getGenres() != null && !serie.getGenres().isEmpty()) {
            for (int i = 0; i < serie.getGenres().size() - 1; i++) {
                binding.genre.setText(binding.genre.getText() + String.valueOf(serie.getGenres().get(i)).substring(String.valueOf(serie.getGenres().get(i)).indexOf("name") + 5, String.valueOf(serie.getGenres().get(i)).toString().length() - 1) + ", ");

            }
            binding.genre.setText(binding.genre.getText() + String.valueOf(serie.getGenres().get(serie.getGenres().size() - 1)).substring(String.valueOf(serie.getGenres().get(serie.getGenres().size() - 1)).indexOf("name") + 5, String.valueOf(serie.getGenres().get(serie.getGenres().size() - 1)).toString().length() - 1) + ".");
        }

        binding.release.setText(String.valueOf(serie.getLast_air_date()));
        checkFavourites(serie);

    }

    public void checkFavourites(final TVShowDetail serie) {
        DbHelper dbHelper = new DbHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean found = false;

        if (db != null){
            String[] columns = { "_id", "idFav"};
            String selection = "idFav = ?";
            String[] selectionArgs = { String.valueOf(serie.getId()) };
            Cursor favouriteMovies = db.query("series", columns, selection, selectionArgs, null, null, null );

            while(favouriteMovies.moveToNext()) {
                found = true;
                binding.cbFavorite.setChecked(true);
            }
            favouriteMovies.close();
        }

        final boolean finalFound = found;
        binding.cbFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (db != null) {
                    if(isChecked){

                        if (!finalFound){
                            db.execSQL("INSERT INTO series (idFav) VALUES ('" + String.valueOf(serie.getId()) + "')");
                            binding.cbFavorite.setChecked(true);
                        } else {
                            binding.cbFavorite.setChecked(true);
                        }

                    } else if(isChecked == false) {
                        db.execSQL("DELETE FROM series WHERE idFav = '"+ String.valueOf(serie.getId()) + "';");
                    }
                }
            }
        });
    }

    public void loadSearch () {
        int idReal = (int) id;

        MovieService movieService = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<TVShowDetail> call = movieService.getSerieByID(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));

        call.enqueue(new Callback<TVShowDetail>() {
            @Override
            public void onResponse(Call<TVShowDetail> call, Response<TVShowDetail> response) {
                switch (response.code()) {
                    case 200:
                        TVShowDetail serie = response.body();
                        bindSerie(serie);
                        binding.progressLoading.setVisibility(View.INVISIBLE);
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<TVShowDetail> call, Throwable t) {
                Toast.makeText(SerieActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadCast() {

        int idReal = (int) id;

        MovieService movieService = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<CreditsFeed> call = movieService.getSeriesCast(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));

        call.enqueue(new Callback<CreditsFeed>() {
            @Override
            public void onResponse(Call<CreditsFeed> call, Response<CreditsFeed> response) {
                switch (response.code()) {

                    case 200:
                        CreditsFeed credits = response.body();
                        //TODO(5): Cargar Casting
                        serieAdapter.swap(credits.getCast());
                        serieAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<CreditsFeed> call, Throwable t) {
                Toast.makeText(serieAdapter.context, "Error", Toast.LENGTH_SHORT).show();

            }

        });
    }

    private void loadTrailer() {

        int idReal = (int) id;
        MovieService movieService = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<Videos> call = movieService.getSerieTrailer(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));

        call.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> call, Response<Videos> response) {
                switch (response.code()) {
                    case 200:
                        Videos data = response.body();
                        for(Result result :data.getResults()){
                            allVideos.add(result);
                        }
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                Toast.makeText(serieAdapter.context, "Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void loadSimilarSeries() {
        int idReal = (int) id;

        MovieService getSerie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);

        Call<TVShowFeed> call = getSerie.getSimilarSeries(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));
        //TODO(6): Crear objeto retrofit y llamar a conseguir las series similares
        call.enqueue(new Callback<TVShowFeed>() {
            @Override
            public void onResponse(Call<TVShowFeed> call, Response<TVShowFeed> response) {
                switch (response.code()) {
                    case 200:
                        TVShowFeed data = response.body();
                        similar.swap(data.getResults());
                        similar.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<TVShowFeed> call, Throwable t) {
                Toast.makeText(serieAdapter.context, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
