package com.example.practicaandroid;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.practicaandroid.databinding.ActivityMovieBinding;
import com.example.practicaandroid.db.DbHelper;
import com.example.practicaandroid.db.models.TVShowDetail;
import com.example.practicaandroid.ui.Videos.Result;
import com.example.practicaandroid.ui.Videos.Videos;
import com.example.practicaandroid.db.adapaters.CastAdapter;
import com.example.practicaandroid.db.adapaters.MovieAdapter;
import com.example.practicaandroid.db.models.CreditsFeed;
import com.example.practicaandroid.db.models.MovieDetail;
import com.example.practicaandroid.db.models.MovieFeed;
import com.example.practicaandroid.retrofit.MovieService;
import com.example.practicaandroid.retrofit.RetrofitInstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieActivity extends AppCompatActivity {

    float id = 0;

    MovieAdapter similarAdapter;
    CastAdapter movieAdapter;


    List<Result> allVideos = new ArrayList<>();
    Result trailer = new Result();
    ActivityMovieBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityMovieBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        id = intent.getFloatExtra("id", 0);

        binding.movieImage.setVisibility(View.INVISIBLE);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.rvRecommendSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommendSeries.setHasFixedSize(true);
        binding.rvRecommendSeries.addItemDecoration(new DividerItemDecoration(binding.rvRecommendSeries.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvRecommendSeries.setItemAnimator(new DefaultItemAnimator());
        binding.rvRecommendSeries.setAdapter(movieAdapter);


        binding.rvCasting.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCasting.setHasFixedSize(true);
        binding.rvCasting.addItemDecoration(new DividerItemDecoration(binding.rvCasting.getContext(), DividerItemDecoration.VERTICAL));
        binding.rvCasting.setItemAnimator(new DefaultItemAnimator());
        binding.rvCasting.setAdapter(similarAdapter);

        movieAdapter = new CastAdapter(this);
        similarAdapter = new MovieAdapter(this);

        loadSimilarMovies();
        loadCast();
        loadTrailer();
        loadSearch();

    }

    public void bindMovie(final MovieDetail movie){

        binding.tvTitle.setText(String.valueOf(movie.getTitle()));
        binding.sinopsis.setText(String.valueOf(movie.getOverview()));

        if(movie.getPoster_path() != null){
            Picasso.get().load("https://image.tmdb.org/t/p/w500" + movie.getPoster_path()).into(binding.movieImage);
        } else {
            Picasso.get().load(R.mipmap.ic_movie_without_photo).into(binding.movieImage);
        }


        float punctuation = (movie.getVote_average()) / 2;
        binding.ratingBar2.setNumStars(5);
        binding.ratingBar2.setRating(punctuation);

        binding.studio.setText("");
        if(movie.getProductionCompanies() != null && !movie.getProductionCompanies().isEmpty()) {
            for (int i = 0; i < movie.getProductionCompanies().size() - 1; i++) {
                binding.studio.setText(binding.studio.getText() + String.valueOf(movie.getProductionCompanies().get(i)).substring(String.valueOf(movie.getProductionCompanies().get(i)).indexOf("name") + 5, String.valueOf(movie.getProductionCompanies().get(i)).indexOf("origin_country") - 2) + ", ");

            }
            binding.studio.setText(binding.studio.getText() + String.valueOf(movie.getProductionCompanies().get(movie.getProductionCompanies().size() - 1)).substring(String.valueOf(movie.getProductionCompanies().get(movie.getProductionCompanies().size() - 1)).indexOf("name") + 5, String.valueOf(movie.getProductionCompanies().get(movie.getProductionCompanies().size() - 1)).indexOf("origin_country") - 2) + ".");
        }


        if (allVideos != null) {
            for (int i = 0; i < allVideos.size(); i++) {
                if (allVideos.get(i).getType().equals("Trailer")) {
                    trailer = allVideos.get(i);
                }
            }
        }

        binding.btWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO(2): Abrir el trailer en Youtube

                if(trailer != null) {
                    Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(MovieActivity.this, getString(R.string.trailer_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.genre.setText("");
        if(movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            for (int i = 0; i < movie.getGenres().size() - 1; i++) {
                binding.genre.setText(binding.genre.getText() + String.valueOf(movie.getGenres().get(i)).substring(String.valueOf(movie.getGenres().get(i)).indexOf("name") + 5, String.valueOf(movie.getGenres().get(i)).toString().length() - 1) + ", ");
            }
            binding.genre.setText(binding.genre.getText() + String.valueOf(movie.getGenres().get(movie.getGenres().size() - 1)).substring(String.valueOf(movie.getGenres().get(movie.getGenres().size() - 1)).indexOf("name") + 5, String.valueOf(movie.getGenres().get(movie.getGenres().size() - 1)).toString().length() - 1) + ".");
        }

        binding.release.setText(String.valueOf(movie.getRelease_date()));

        checkFavourites(movie);
    }

    public void checkFavourites(final MovieDetail movie) {
        DbHelper dbHelper = new DbHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean found = false;

        if(db != null){
            String[] columns = {"_id", "idFav"};
            String selection = "idFav = ?";
            String[] selectionArgs = { String.valueOf(movie.getId()) };
            Cursor favouriteMovies = db.query("favIds", columns, selection, selectionArgs, null, null, null );

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
                    if (isChecked){

                        if(!finalFound){
                            db.execSQL("INSERT INTO favIds (idFav) VALUES ('" + movie.getId() + "')");
                            binding.cbFavorite.setChecked(true);
                        } else {
                            binding.cbFavorite.setChecked(true);
                        }

                    } else {
                        db.execSQL("DELETE FROM favIds WHERE idFav = '"+ String.valueOf(movie.getId()) + "';");
                    }
                }
            }
        });
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
                        //TODO(3): Mostrar la búsqueda
                        MovieDetail pelicula = response.body();
                        bindMovie(pelicula);
                        binding.movieImage.setVisibility(View.VISIBLE);
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Toast.makeText(MovieActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCast() {

        int idReal = (int) id;
        MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);

        Call<CreditsFeed> call = getMovie.getMovieCast(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));
        call.enqueue(new Callback<CreditsFeed>() {
            @Override
            public void onResponse(Call<CreditsFeed> call, Response<CreditsFeed> response) {
                switch (response.code()) {
                    case 200:
                        CreditsFeed credits = response.body();
                        //TODO(4): Mostrar el casting
                        movieAdapter.swap(credits.getCast());
                        movieAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<CreditsFeed> call, Throwable t) {
                Toast.makeText(movieAdapter.context, "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadTrailer() {

        int idReal = (int) id;
        MovieService movieService = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        /* Se definen los parámetros de la llamada a la función getTopRated */
        Call<Videos> call = movieService.getMovieTrailer(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));
        /* Se hace una llamada asíncrona a la API */
        call.enqueue(new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> call, Response<Videos> response) {
                Log.i("Response", String.valueOf(response));
                switch (response.code()) {
                    /* En caso de respuesta correcta */
                    case 200:
                        /* En el objeto data de la clase MovieFeed se almacena el JSON convertido a objetos*/
                        Videos data = response.body();
                        /* Se actualizan los datos contenidos en el adaptador */
                        for(Result result :data.getResults()){
                            allVideos.add(result);
                        }
                        Log.i("todos videos", String.valueOf(allVideos.size()));
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                Toast.makeText(movieAdapter.context, "Error loading the trailer...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void loadSimilarMovies(){
        /* Crea la instanncia de retrofit */
        int idReal = (int) id;

        /* Se definen los parámetros de la llamada a la función getTopRated */ MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<MovieFeed> call = getMovie.getSimilarMovies(idReal, RetrofitInstance.getApiKey(), getString(R.string.language));
        /* Se hace una llamada asíncrona a la API */
        call.enqueue(new Callback<MovieFeed>() {
            @Override
            public void onResponse(Call<MovieFeed> call, Response<MovieFeed> response) {
                Log.i("Response", String.valueOf(response));
                switch (response.code()) {
                    /* En caso de respuesta correcta */
                    case 200:
                        /* En el objeto data de la clase MovieFeed se almacena el JSON convertido a objetos*/
                        MovieFeed data = response.body();
                        /* Se actualizan los datos contenidos en el adaptador */
                        similarAdapter.swap(data.getResults());
                        similarAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieFeed> call, Throwable t) {
                Toast.makeText(movieAdapter.context, "Error loading the trailer...", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
