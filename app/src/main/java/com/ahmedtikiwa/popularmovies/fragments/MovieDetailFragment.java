package com.ahmedtikiwa.popularmovies.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmedtikiwa.popularmovies.BuildConfig;
import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.adapters.MovieReviewAdapter;
import com.ahmedtikiwa.popularmovies.adapters.MovieTrailerAdapter;
import com.ahmedtikiwa.popularmovies.api.TmdbApi;
import com.ahmedtikiwa.popularmovies.data.MovieContract;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.models.MovieReview;
import com.ahmedtikiwa.popularmovies.models.MovieReviewResponse;
import com.ahmedtikiwa.popularmovies.models.MovieTrailer;
import com.ahmedtikiwa.popularmovies.models.MovieTrailersResponse;
import com.ahmedtikiwa.popularmovies.utils.Constants;
import com.ahmedtikiwa.popularmovies.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final String MOVIE_PARCEL = "movie";
    private static final int MOVIE_LOADER = 0;
    public static final String MOVIE_DETAIL_URI = "URI";
    private Movie movie;
    private FloatingActionButton fab;
    private MovieReviewAdapter movieReviewAdapter;
    private MovieTrailerAdapter movieTrailerAdapter;
    private Uri mUri;
    private long movieId;
    public static final int DETAIL_LOADER = 0;
    private ImageView movieBackdrop, moviePoster;
    private TextView plotSynopsis, releaseDate, voteAverage;
    private ProgressBar backdropProgress, posterProgress, reviewProgress, trailerProgress;
    private LinearLayout emptyReviewStateLayer, emptyTrailerStateLayer;
    private ListView reviewList, trailerList;
    private ArrayList<MovieReview> movieReviewsList;
    private ArrayList<MovieTrailer> movieTrailerList;

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

    public MovieDetailFragment() {
    }

    public void onSortOrderPrefChange() {
        Uri uri = mUri;
        if (null != uri) {
            if (Utility.userPrefersFavoriteMovies(getActivity())) {
                getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_PARCEL, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            if (Utility.userPrefersFavoriteMovies(getActivity())) {
                mUri = args.getParcelable(MovieDetailFragment.MOVIE_DETAIL_URI);
            } else {
                Intent intent = getActivity().getIntent();
                movie = intent.getExtras().getParcelable(getString(R.string.movies_parcel));
            }

        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setupUIElements(rootView);
        movieReviewsList = new ArrayList<MovieReview>();
        movieReviewAdapter = new MovieReviewAdapter(getActivity(), 0, movieReviewsList);
        reviewList.setAdapter(movieReviewAdapter);

        movieTrailerList = new ArrayList<MovieTrailer>();
        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), 0, movieTrailerList);
        trailerList.setAdapter(movieTrailerAdapter);

        if (movie != null && !Utility.userPrefersFavoriteMovies(getActivity())) {
            getActivity().setTitle(movie.getTitle());
            loadDetail();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues movieData = new ContentValues();
                    movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                    movieData.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                    movieData.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                    movieData.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                    movieData.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                    movieData.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                    movieData.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

                    Uri insertedUri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieData);

                    movieId = ContentUris.parseId(insertedUri);

                }
            });
        }

        return rootView;
    }

    private void loadDetail() {
        if (movie != null) {
            // setup the backdrop
            String backdropUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_BACKDROP_SIZE + movie.getBackdropPath();
            String posterUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_RECOMMENDED_SIZE + movie.getPosterPath();
            Glide.with(getContext())
                    .load(backdropUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            backdropProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(movieBackdrop);

            // setup the plot synopsis
            plotSynopsis.setText(movie.getOverview());
            // setup the movie poster
            Glide.with(getContext())
                    .load(posterUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            posterProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(moviePoster);
            // setup the movie release date
            releaseDate.setText(movie.getReleaseDate());
            // setup the vote average
            voteAverage.setText(String.format(getString(R.string.vote_average), movie.getVoteAverage()));

            if (movie.getId() != null) {
                loadMovieReviews(movie.getId());
                loadMovieTrailers(movie.getId());
            }

        }
    }

    private void loadMovieTrailers(int movieId) {
        Call<MovieTrailersResponse> movieTrailersResponse = TmdbApi.getTmdbApiClient().videos(movieId, BuildConfig.TMDB_API_KEY);
        movieTrailersResponse.enqueue(new Callback<MovieTrailersResponse>() {
            @Override
            public void onResponse(Call<MovieTrailersResponse> call, Response<MovieTrailersResponse> response) {
                if (response.isSuccessful()) {
                    MovieTrailersResponse movieTrailersResponse = response.body();
                    ArrayList trailers = movieTrailersResponse.getResults();

                    updateTrailersData(trailers);
                } else {
                    trailerProgress.setVisibility(ProgressBar.GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieTrailersResponse> call, Throwable t) {
                emptyTrailerStateLayer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadMovieReviews(int movieId) {
        Call<MovieReviewResponse> movieReviewResponse = TmdbApi.getTmdbApiClient().reviews(movieId, BuildConfig.TMDB_API_KEY);
        movieReviewResponse.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                if (response.isSuccessful()) {
                    MovieReviewResponse movieReviewResponse = response.body();
                    ArrayList reviews = movieReviewResponse.getResults();

                    updateReviewsData(reviews);
                } else {
                    reviewProgress.setVisibility(ProgressBar.GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {
                emptyReviewStateLayer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupUIElements(View rootView) {
        movieBackdrop = (ImageView) rootView.findViewById(R.id.movie_backdrop);
        plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
        moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        releaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        voteAverage = (TextView) rootView.findViewById(R.id.movie_vote_average);
        backdropProgress = (ProgressBar) rootView.findViewById(R.id.backdrop_progress);
        posterProgress = (ProgressBar) rootView.findViewById(R.id.poster_progress);
        reviewProgress = (ProgressBar) rootView.findViewById(R.id.reviews_progress);
        trailerProgress = (ProgressBar) rootView.findViewById(R.id.trailers_progress);
        reviewList = (ListView) rootView.findViewById(R.id.movie_reviews_list);
        trailerList = (ListView) rootView.findViewById(R.id.movie_trailers_list);
        emptyReviewStateLayer = (LinearLayout)rootView.findViewById(R.id.empty_reviews_state_layer);
        emptyTrailerStateLayer = (LinearLayout)rootView.findViewById(R.id.empty_trailers_state_layer);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

    }

    private void updateReviewsData(ArrayList arrayList) {
        if (arrayList != null) {
            movieReviewsList.clear();
            movieReviewsList.addAll(arrayList);

            movieReviewAdapter.notifyDataSetChanged();

            reviewList.setVisibility(View.VISIBLE);
            reviewProgress.setVisibility(ProgressBar.GONE);
        }
    }

    private void updateTrailersData(ArrayList arrayList) {
        if (arrayList != null) {
            movieTrailerList.clear();
            movieTrailerList.addAll(arrayList);

            movieTrailerAdapter.notifyDataSetChanged();

            trailerList.setVisibility(View.VISIBLE);
            trailerProgress.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToNext()) {
            fab.setImageResource(android.R.drawable.btn_star_big_on);

            getActivity().setTitle(data.getString(COLUMN_TITLE));
            // setup the backdrop
            String backdropUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_BACKDROP_SIZE + data.getString(COLUMN_BACKDROP_PATH);
            String posterUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_RECOMMENDED_SIZE + data.getString(COLUMN_POSTER_PATH);
            Glide.with(getContext())
                    .load(backdropUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            backdropProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(movieBackdrop);

            // setup the plot synopsis
            plotSynopsis.setText(data.getString(COLUMN_OVERVIEW));

            // setup the movie poster
            Glide.with(getContext())
                    .load(posterUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            posterProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(moviePoster);

            loadMovieReviews(data.getInt(COL_MOVIE_ID));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}