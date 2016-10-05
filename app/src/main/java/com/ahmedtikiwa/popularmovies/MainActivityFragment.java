package com.ahmedtikiwa.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES_PARCEL)) {
            movieArrayList = new ArrayList<Movie>();
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

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(adapter);

        loadMovies();

        return rootView;
    }

    public void loadMovies() {
        Call<MoviesResponse> call = TmdbApi.getTmdbApiClient().popularMovies(BuildConfig.TMDB_API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    MoviesResponse moviesResponse = response.body();
                    movieArrayList = moviesResponse.getResults();
                    adapter.notifyDataSetChanged();

                } else {
                    Log.d(LOG_TAG, "Response was not successful: " + String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }
}
