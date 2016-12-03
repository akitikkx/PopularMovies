package com.ahmedtikiwa.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.fragments.MovieDetailFragment;
import com.ahmedtikiwa.popularmovies.utils.Utility;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            if (Utility.userPrefersFavoriteMovies(this)) {
                args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, getIntent().getData());
            } else {
                args.putParcelable(getString(R.string.movies_parcel), getIntent().getParcelableExtra(getString(R.string.movies_parcel)));
            }

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, movieDetailFragment)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
