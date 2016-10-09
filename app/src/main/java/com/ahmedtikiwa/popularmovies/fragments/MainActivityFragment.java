package com.ahmedtikiwa.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ahmedtikiwa.popularmovies.BuildConfig;
import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.adapters.MoviesListAdapter;
import com.ahmedtikiwa.popularmovies.api.TmdbApi;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.models.MoviesResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityFragment extends Fragment {

    public static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayList<Movie> movieArrayList;
    private static final String MOVIES_PARCEL = "movies";
    private MoviesListAdapter adapter;
    private GridView mGridView;
    private ProgressBar progressBar;
    private SharedPreferences sharedPrefs;
    private LinearLayout emptyStateLayer;

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

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        setupUIElements(rootView);

        adapter = new MoviesListAdapter(getActivity(), 0, movieArrayList);

        mGridView.setAdapter(adapter);

        loadMovies();

        return rootView;
    }

    private void setupUIElements(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        emptyStateLayer = (LinearLayout)view.findViewById(R.id.empty_state_layer);
    }

    /**
     * Fetches the movie list data from the server
     */
    public void loadMovies() {
        // while processing, display the progressbar and hide the gridview
        // until its populated
        progressBar.setVisibility(ProgressBar.VISIBLE);
        mGridView.setVisibility(View.GONE);

        String sortOrderPreference = sharedPrefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_default_movies_sort_order));

        if (sortOrderPreference.equals(getString(R.string.pref_default_movies_sort_order))) {
            Call<MoviesResponse> popularMovies = TmdbApi.getTmdbApiClient().popularMovies(BuildConfig.TMDB_API_KEY);
            loadMoviesPreference(popularMovies);
            getActivity().setTitle(R.string.pref_popular_movies_title);
        } else {
            Call<MoviesResponse> topRatedMovies = TmdbApi.getTmdbApiClient().topRatedMovies(BuildConfig.TMDB_API_KEY);
            loadMoviesPreference(topRatedMovies);
            getActivity().setTitle(R.string.pref_top_rated_movies_title);
        }
    }

    /**
     * Loads the movies based on the shared pref value - Popular or Most Rated
     *
     * @param call
     */
    private void loadMoviesPreference(Call<MoviesResponse> call) {
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    MoviesResponse moviesResponse = response.body();
                    ArrayList movies = moviesResponse.getResults();

                    updateData(movies);
                    emptyStateLayer.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(ProgressBar.GONE);
                    emptyStateLayer.setVisibility(View.VISIBLE);
                    Log.d(LOG_TAG, "Response was not successful: " + String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                emptyStateLayer.setVisibility(View.VISIBLE);
                Log.d(LOG_TAG, String.valueOf(t.getMessage()));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // cater for the pref value being changed and the user returning
        // to the list screen
        loadMovies();
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