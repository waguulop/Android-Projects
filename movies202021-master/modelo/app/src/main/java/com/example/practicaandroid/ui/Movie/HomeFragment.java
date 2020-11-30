package com.example.practicaandroid.ui.Movie;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicaandroid.db.adapaters.FindMovieAdapter;
import com.example.practicaandroid.R;
import com.example.practicaandroid.db.adapaters.MovieAdapter;
import com.example.practicaandroid.db.models.MovieFeed;
import com.example.practicaandroid.retrofit.MovieService;
import com.example.practicaandroid.retrofit.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    MovieAdapter movieAdapter2;
    FindMovieAdapter movieAdapter3;
    RecyclerView recyclerView2;
    RecyclerView recyclerView3;
    ProgressBar progressBar;
    EditText editText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_media_final, container, false);

        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);



        /* Enlaza el RecyclerView definido en el layout con el objeto recyclerView */
        recyclerView = (RecyclerView) root.findViewById(R.id.rv_recommend_movies);
        recyclerView2 = (RecyclerView) root.findViewById(R.id.rv_recommend_series);
        recyclerView3 = (RecyclerView) root.findViewById(R.id.recycler_view_busqueda);
        recyclerView3.setVisibility(View.INVISIBLE);

        /* Establece que recyclerView tendrá un layout lineal, en concreto vertical*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        /*  Indica que cada uno de los items va a tener un tamaño fijo*/
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setHasFixedSize(true);


        /* Establece la  decoración por defecto de cada uno de lo items: una línea de división*/
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView2.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView3.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        /* Instancia un objeto de la clase MovieAdapter */
        movieAdapter = new MovieAdapter(this.getContext());
        movieAdapter2 = new MovieAdapter(this.getContext());
        movieAdapter3 = new FindMovieAdapter(this.getContext());

        /* Establece el adaptador asociado a recyclerView */
        recyclerView.setAdapter(movieAdapter);
        recyclerView2.setAdapter(movieAdapter2);
        recyclerView3.setAdapter(movieAdapter3);


        /* Pone la animación por defecto de recyclerView */
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setItemAnimator(new DefaultItemAnimator());

        editText = (EditText) root.findViewById(R.id.et_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    recyclerView3.setVisibility(View.VISIBLE);
                    loadBusqueda(s);
                } else if(s.length() == 0){
                    recyclerView3.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        loadSearch();

        return root;
    }

    public void loadSearch () {
        /* Crea la instanncia de retrofit */
        MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        /* Se definen los parámetros de la llamada a la función getTopRated */
        Call<MovieFeed> call2 = getMovie.getMovies(RetrofitInstance.getApiKey(), getContext().getString(R.string.language));
        Call<MovieFeed> call = getMovie.getTopRated(RetrofitInstance.getApiKey(), getContext().getString(R.string.language));

        /* Se hace una llamada asíncrona a la API */
        call.enqueue(new Callback<MovieFeed>() {
            @Override
            public void onResponse(Call<MovieFeed> call, Response<MovieFeed> response) {
                switch (response.code()) {
                    /* En caso de respuesta correcta */
                    case 200:
                        MovieFeed data = response.body();
                        movieAdapter.swap(data.getResults());
                        movieAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieFeed> call, Throwable t) {
                Toast.makeText(movieAdapter.context, "Error loading top rated movies...", Toast.LENGTH_SHORT).show();

            }
        });

        /* Se hace una llamada asíncrona a la API */
        call2.enqueue(new Callback<MovieFeed>() {
            @Override
            public void onResponse(Call<MovieFeed> call, Response<MovieFeed> response) {
                switch (response.code()) {
                    /* En caso de respuesta correcta */
                    case 200:
                        MovieFeed data = response.body();
                        movieAdapter2.swap(data.getResults());
                        progressBar.setVisibility(View.INVISIBLE);
                        movieAdapter2.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieFeed> call, Throwable t) {
                Toast.makeText(movieAdapter2.context, "Error loading popular movies...", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void loadBusqueda(CharSequence s){

        String nombre = s.toString();
        MovieService getMovie = RetrofitInstance.getRetrofitInstance().create(MovieService.class);
        Call<MovieFeed> call = getMovie.getMovieByName(RetrofitInstance.getApiKey(), getContext().getString(R.string.language), s.toString());

        call.enqueue(new Callback<MovieFeed>() {
            @Override
            public void onResponse(Call<MovieFeed> call, Response<MovieFeed> response) {
                switch (response.code()) {
                    case 200:
                        MovieFeed data = response.body();
                        movieAdapter3.swap(data.getResults());
                        movieAdapter3.notifyDataSetChanged();
                        break;
                    case 401:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieFeed> call, Throwable t) {
                Toast.makeText(movieAdapter3.context, "Error finding...", Toast.LENGTH_SHORT).show();

            }
        });
    }

}