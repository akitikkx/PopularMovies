package com.ahmedtikiwa.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.ahmedtikiwa.popularmovies.adapters.MoviesListAdapter;
import com.ahmedtikiwa.popularmovies.api.TmdbApi;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.models.MoviesResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayList<Movie> movieArrayList;
    private static final String MOVIES_PARCEL = "movies";
    private MoviesListAdapter adapter;
    private GridView mGridView;
    private ProgressBar progressBar;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if no bundle exists in the savedInstanceState then a new array will be created
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_PARCEL)) {
            movieArrayList = new ArrayList<Movie>();
        // use the data from the savedInstanceState
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList(MOVIES_PARCEL);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_PARCEL, movieArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new MoviesListAdapter(getActivity(), 0, movieArrayList);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(adapter);

        loadMovies();

        return rootView;
    }

    /**
     * Fetches the movie list data from the server
     */
    public void loadMovies() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        Call<MoviesResponse> call = TmdbApi.getTmdbApiClient().popularMovies(BuildConfig.TMDB_API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    MoviesResponse moviesResponse = response.body();
                    ArrayList movies = moviesResponse.getResults();

                    updateData(movies);

                } else {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Log.d(LOG_TAG, "Response was not successful: " + String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    private void updateData(ArrayList arrayList) {
        if (arrayList != null) {
            movieArrayList.clear();
            movieArrayList.addAll(arrayList);

            adapter.notifyDataSetChanged();

            mGridView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }
}
