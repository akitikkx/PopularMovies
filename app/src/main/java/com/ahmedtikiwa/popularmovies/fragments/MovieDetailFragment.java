package com.ahmedtikiwa.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final String MOVIE_PARCEL = "movie";
    private Movie movie;
    private ImageView movieBackdrop, moviePoster;
    private TextView plotSynopsis, releaseDate, voteAverage;
    private ProgressBar backdropProgress, posterProgress;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_PARCEL)) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                movie = intent.getParcelableExtra(getString(R.string.movies_parcel));
            }
        } else {
            movie = savedInstanceState.getParcelable(MOVIE_PARCEL);
        }
        getActivity().setTitle(movie.getTitle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_PARCEL, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setupUIElements(rootView);

        if (movie != null) {
            loadDetail();
        }

        return rootView;
    }

    private void loadDetail() {
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
    }

    private void setupUIElements(View rootView) {
        movieBackdrop = (ImageView) rootView.findViewById(R.id.movie_backdrop);
        plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
        moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        releaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        voteAverage = (TextView) rootView.findViewById(R.id.movie_vote_average);
        backdropProgress = (ProgressBar)rootView.findViewById(R.id.backdrop_progress);
        posterProgress = (ProgressBar)rootView.findViewById(R.id.poster_progress);
    }
}