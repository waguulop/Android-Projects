package com.example.practicaandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practicaandroid.ui.CastClassesToMovie.CastMovieCredits;
import com.example.practicaandroid.db.adapaters.CastAdapterMovie;
import com.example.practicaandroid.db.adapaters.CastAdapterSerie;
import com.example.practicaandroid.db.models.CreditsDetail;
import com.example.practicaandroid.retrofit.MovieService;
import com.example.practicaandroid.retrofit.RetrofitInstance;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorActivity extends AppCompatActivity {

    float id;
    TextView name;
    TextView biography;
    TextView birthday;
    ImageView photo;
    TextView place_of_birth;
    TextView popularity;
    TextView knownFor;
    TextView knownAs;

    RecyclerView recyclerView;
    RecyclerView recyclerView2;

    CastAdapterMovie movieAdapter;
    CastAdapterSerie serieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_actor);

        Intent intent = getIntent();

        id = intent.getFloatExtra("id", 0);
        name = (TextView) findViewById(R.id.tv_title);
        biography = (TextView) findViewById(R.id.studio);
        birthday = (TextView) findViewById(R.id.sinopsis);
        photo = (ImageView) findViewById(R.id.movieImage);
        place_of_birth = (TextView) findViewById(R.id.textView12);
        popularity = (TextView) findViewById(R.id.textView14);
        knownFor = (TextView) findViewById(R.id.textView16);
        knownAs = (TextView) findViewById(R.id.textView18);


        recyclerView = (RecyclerView) findViewById(R.id.moviesActors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView2 = (RecyclerView) findViewById(R.id.seriesActors);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView2.addItemDecoration(new DividerItemDecoration(recyclerView2.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView2.setAdapter(serieAdapter);

        movieAdapter = new CastAdapterMovie(this);
        serieAdapter = new CastAdapterSerie(this);

        recyclerView.setAdapter(movieAdapter);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());

        loadRecycler2();
        loadRecycler();
        loadSearch();
    }

    public void loadRecycler(){
        int idReal = (int) id;

        MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<CastMovieCredits> call = getMovie.getActorCredits(idReal, RetrofitInstance.getApiKey(), "en");
        call.enqueue(new Callback<CastMovieCredits>() {
            @Override
            public void onResponse(Call<CastMovieCredits> call, Response<CastMovieCredits> response) {
                switch (response.code()) {
                    case 200:
                        CastMovieCredits data = response.body();
                        movieAdapter.swap(data.getCast());
                        movieAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<CastMovieCredits> call, Throwable t) {
                Toast.makeText(ActorActivity.this, "Error cargando películas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadRecycler2(){
        int idReal = (int) id;
        MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<CastMovieCredits> call = getMovie.getActorSeriesCredits(idReal, RetrofitInstance.getApiKey(), "en");
        call.enqueue(new Callback<CastMovieCredits>() {
            @Override
            public void onResponse(Call<CastMovieCredits> call, Response<CastMovieCredits> response) {
                switch (response.code()) {
                    case 200:
                        CastMovieCredits data = response.body();
                        serieAdapter.swap(data.getCast());
                        serieAdapter.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<CastMovieCredits> call, Throwable t) {
                Toast.makeText(ActorActivity.this, "Error cargando películas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadSearch () {

        int idReal = (int) id;
        MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<CreditsDetail> call = getMovie.getActorByID(idReal, RetrofitInstance.getApiKey(), "en");
        call.enqueue(new Callback<CreditsDetail>() {
            @Override
            public void onResponse(Call<CreditsDetail> call, Response<CreditsDetail> response) {
                Log.i("Response", String.valueOf(response));
                switch (response.code()) {
                    case 200:
                        CreditsDetail data = response.body();
                        loadActor(data);
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<CreditsDetail> call, Throwable t) {
                Toast.makeText(ActorActivity.this, "Error cargando películas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadActor(CreditsDetail actor){

        name.setText(actor.getName());
        birthday.setText(actor.getBirthday());
        biography.setText(actor.getBiography());
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + actor.getProfilePath()).into(photo);
        place_of_birth.setText(actor.getPlaceOfBirth());
        popularity.setText(String.valueOf(actor.getPopularity()));
        knownFor.setText(actor.getKnownForDepartment());

        if(actor.getAlsoKnownAs().toString().length() != 0) {
            knownAs.setText(String.valueOf(actor.getAlsoKnownAs().toString().substring(1, actor.getAlsoKnownAs().toString().length() - 1)));
            knownAs.setText(knownAs.getText() + ".");
        }

    }
}
