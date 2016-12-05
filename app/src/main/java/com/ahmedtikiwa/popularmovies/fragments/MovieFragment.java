package com.ahmedtikiwa.popularmovies.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ahmedtikiwa.popularmovies.BuildConfig;
import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.adapters.FavoriteListAdapter;
import com.ahmedtikiwa.popularmovies.adapters.MovieListAdapter;
import com.ahmedtikiwa.popularmovies.api.TmdbApi;
import com.ahmedtikiwa.popularmovies.data.MovieContract;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.models.MoviesResponse;
import com.ahmedtikiwa.popularmovies.utils.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private ArrayList<Movie> movieArrayList;
    private static final String MOVIES_PARCEL = "movies";
    private FavoriteListAdapter favoriteListAdapter;
    private MovieListAdapter movieListAdapter;
    private GridView mGridView;
    private ProgressBar progressBar;
    private SharedPreferences sharedPrefs;
    private LinearLayout emptyStateLayer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mPosition = GridView.INVALID_POSITION;
    private static final int MOVIE_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_POSTER_PATH = 3;
    public static final int COLUMN_BACKDROP_PATH = 4;
    public static final int COLUMN_OVERVIEW = 5;
    public static final int COLUMN_VOTE_AVERAGE = 6;
    public static final int COLUMN_RELEASE_DATE = 7;

    public MovieFragment() {
    }

    public interface Callback {
        public void onFavoriteItemSelected(Uri movieUri);

        public void onItemSelected(Bundle bundle);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (Utility.userPrefersFavoriteMovies(getActivity())) {
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSortOrderPrefChange() {
        refreshData();
    }

    private void refreshData() {
        if (Utility.userPrefersFavoriteMovies(getActivity())) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        } else {
            loadMovies();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // cater for the pref value being changed and the user returning
        // to the list screen
        loadMovies();
    }

    /**
     * Fetches the movie list data from the server
     */
    public void loadMovies() {
        // while processing, display the progressbar and hide the gridview
        // until its populated

        if (!Utility.userPrefersFavoriteMovies(getActivity())) {
            progressBar.setVisibility(ProgressBar.VISIBLE);

            if (Utility.userPrefersPopularMovies(getActivity())) {
                Call<MoviesResponse> popularMovies = TmdbApi.getTmdbApiClient().popularMovies(BuildConfig.TMDB_API_KEY);
                loadMoviesPreference(popularMovies);
                getActivity().setTitle(R.string.pref_popular_movies_title);
            } else {
                Call<MoviesResponse> topRatedMovies = TmdbApi.getTmdbApiClient().topRatedMovies(BuildConfig.TMDB_API_KEY);
                loadMoviesPreference(topRatedMovies);
                getActivity().setTitle(R.string.pref_top_rated_movies_title);
            }
        } else {
            getActivity().setTitle(R.string.pref_favorite_movies_title);
        }

    }

    /**
     * Loads the movies based on the shared pref value - Popular or Most Rated
     *
     * @param call
     */
    private void loadMoviesPreference(Call<MoviesResponse> call) {
        call.enqueue(new retrofit2.Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    MoviesResponse moviesResponse = response.body();
                    ArrayList movies = moviesResponse.getResults();

                    updateData(movies);
                    emptyStateLayer.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);

                } else {
                    progressBar.setVisibility(ProgressBar.GONE);
                    emptyStateLayer.setVisibility(View.VISIBLE);
                    Log.d(LOG_TAG, "Response was not successful: " + String.valueOf(response.errorBody()));
                }

                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                emptyStateLayer.setVisibility(View.VISIBLE);
                Log.d(LOG_TAG, String.valueOf(t.getMessage()));

                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void updateData(ArrayList arrayList) {
        if (arrayList != null) {
            movieArrayList.clear();
            movieArrayList.addAll(arrayList);

            movieListAdapter.notifyDataSetChanged();

            mGridView.setVisibility(View.VISIBLE);
            // catering for switch in adapter
            mGridView.setAdapter(movieListAdapter);
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setupUIElements(rootView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        movieListAdapter = new MovieListAdapter(getActivity(), 0, movieArrayList);
        favoriteListAdapter = new FavoriteListAdapter(getActivity(), null, 0);

        if (Utility.userPrefersFavoriteMovies(getActivity())) {
            mGridView.setAdapter(favoriteListAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    try {
                        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                        if (cursor != null) {
                            ((Callback) getActivity()).onFavoriteItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID)));
                        }
                        mPosition = position;
                    } catch (ClassCastException e) {
                        Log.d(TAG, String.valueOf(e.getMessage()));
                    }
                }
            });
        } else {
            mGridView.setAdapter(movieListAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    try {
                        Movie selectedMovie = (Movie) adapterView.getItemAtPosition(position);
                        if (selectedMovie != null) {
                            Bundle args = new Bundle();
                            args.putParcelable(getString(R.string.movies_parcel), selectedMovie);

                            ((Callback) getActivity()).onItemSelected(args);
                        }
                        mPosition = position;

                    } catch (ClassCastException e) {
                        Log.d(TAG, String.valueOf(e.getMessage()));
                    }
                }
            });
            loadMovies();
        }

        return rootView;
    }

    private void setupUIElements(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        emptyStateLayer = (LinearLayout) view.findViewById(R.id.empty_state_layer);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (progressBar != null) {
            mGridView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (progressBar != null && data.moveToNext()) {
            mGridView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            if (Utility.userPrefersFavoriteMovies(getActivity())) {
                favoriteListAdapter.swapCursor(data);
                // catering for switch in adapter
                mGridView.setAdapter(favoriteListAdapter);
            }

            if (mPosition != GridView.INVALID_POSITION) {
                mGridView.smoothScrollToPosition(mPosition);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
            emptyStateLayer.setVisibility(View.VISIBLE);
        }

        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteListAdapter.swapCursor(null);
    }
}