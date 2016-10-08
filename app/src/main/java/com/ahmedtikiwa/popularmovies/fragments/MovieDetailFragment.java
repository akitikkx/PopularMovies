package com.ahmedtikiwa.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.utils.Constants;
import com.bumptech.glide.Glide;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends android.support.v4.app.Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private Movie movie;
    private ImageView movieBackdrop, moviePoster;
    private TextView plotSynopsis;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            movie = intent.getParcelableExtra(getString(R.string.movies_parcel));
            getActivity().setTitle(movie.getTitle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setupUIElements(rootView);

        if (movie != null) {
            // setup the backdrop
            String posterUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_BACKDROP_SIZE + movie.getBackdropPath();
            Glide.with(getContext()).load(posterUrl).into(movieBackdrop);

            // setup the plot synopsis
            plotSynopsis.setText(movie.getOverview());
        }

        return rootView;
    }

    private void setupUIElements(View rootView) {
        movieBackdrop = (ImageView) rootView.findViewById(R.id.movie_backdrop);
        plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
    }
}
