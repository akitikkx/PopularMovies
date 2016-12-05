package com.ahmedtikiwa.popularmovies.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.StaleDataException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private int selectedMovieId;
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
    private ListView reviewList;
    private RecyclerView trailerList;
    private ArrayList<MovieReview> movieReviewsList;
    private ArrayList<MovieTrailer> movieTrailerList = new ArrayList<MovieTrailer>();

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
                movie = args.getParcelable(getString(R.string.movies_parcel));
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setupUIElements(rootView);
        movieReviewsList = new ArrayList<MovieReview>();
        movieReviewAdapter = new MovieReviewAdapter(getActivity(), 0, movieReviewsList);
        reviewList.setAdapter(movieReviewAdapter);

        movieTrailerAdapter = new MovieTrailerAdapter(getActivity(), movieTrailerList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        trailerList.setNestedScrollingEnabled(false);
        trailerList.setItemAnimator(new DefaultItemAnimator());
        trailerList.setHasFixedSize(true);
        trailerList.setLayoutManager(layoutManager);
        trailerList.setAdapter(movieTrailerAdapter);

        if (movie != null && !Utility.userPrefersFavoriteMovies(getActivity())) {
            selectedMovieId = movie.getId();
            getActivity().setTitle(movie.getTitle());
            loadDetail();
            isMovieFavorited(movie.getId());

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteAction(movie.getId());
                }
            });
        }

        return rootView;
    }

    /**
     * Checks the database whether the movie has been added to the favorites list
     * @param movieId
     * @return
     */
    private boolean isMovieFavorited(int movieId) {
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(movieId)},
                null
        );

        // if the movie is a favorite also set the star to on
        if (movieCursor != null && movieCursor.moveToNext()) {
            if (fab != null) {
                fab.setImageResource(android.R.drawable.btn_star_big_on);
            }
            movieCursor.close();
            return true;
        }

        movieCursor.close();
        return false;
    }

    /**
     * Triggers the addition or removal of a movie from the favorites database
     *
     * @param movieId
     */
    private void favoriteAction(int movieId) {
        if (isMovieFavorited(movieId)) {
            // movie exists and needs to be de-selected as a favorite
            getContext().getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{Integer.toString(movieId)}
            );
            fab.setImageResource(android.R.drawable.btn_star_big_off);
            Toast.makeText(getActivity(), getString(R.string.removed_favorite_movie_success), Toast.LENGTH_LONG).show();
        } else {
            if (movie != null) {
                ContentValues movieData = new ContentValues();
                movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                movieData.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                movieData.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                movieData.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                movieData.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                movieData.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                movieData.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

                Uri insertedUri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieData);

                fab.setImageResource(android.R.drawable.btn_star_big_on);
                Toast.makeText(getActivity(), getString(R.string.add_favorite_movie_success), Toast.LENGTH_LONG).show();
            }
        }
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
        trailerList = (RecyclerView) rootView.findViewById(R.id.movie_trailers_list);
        emptyReviewStateLayer = (LinearLayout) rootView.findViewById(R.id.empty_reviews_state_layer);
        emptyTrailerStateLayer = (LinearLayout) rootView.findViewById(R.id.empty_trailers_state_layer);
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
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (data != null && data.moveToNext()) {
            fab.setImageResource(android.R.drawable.btn_star_big_on);
            selectedMovieId = data.getInt(COL_MOVIE_ID);

            loadMovieTrailers(data.getInt(COL_MOVIE_ID));

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
            releaseDate.setText(data.getString(COLUMN_RELEASE_DATE));
            voteAverage.setText(data.getString(COLUMN_VOTE_AVERAGE));

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

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        // TODO: 2016/12/05 correct issue here with staleDataException
                        favoriteAction(data.getInt(COL_MOVIE_ID));
                        fab.setVisibility(View.GONE);
                    } catch (StaleDataException e) {
                    }
                }
            });

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}