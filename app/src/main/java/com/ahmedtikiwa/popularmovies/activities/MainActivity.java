package com.ahmedtikiwa.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmedtikiwa.popularmovies.App;
import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.fragments.MovieDetailFragment;
import com.ahmedtikiwa.popularmovies.fragments.MovieFragment;
import com.ahmedtikiwa.popularmovies.utils.Utility;

public class MainActivity extends BaseActivity implements MovieFragment.Callback {

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MovieDetailFragment";
    private CoordinatorLayout coordinatorLayout;
    private App app;
    private String mSortOrder;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        app = (App) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        // check if the device is connected to the internet
        checkNetworkConnection(coordinatorLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkConnection(coordinatorLayout);
        String sortOrderPref = Utility.getPreferredSortOrder(this);
        if (sortOrderPref != null && !sortOrderPref.equals(mSortOrder)) {
            MovieFragment movieFragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.movie_container);
            if (null != movieFragment) {
                movieFragment.onSortOrderPrefChange();
            }

            MovieDetailFragment movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
            if (null != movieDetailFragment) {
                movieDetailFragment.onSortOrderPrefChange();
            }
            mSortOrder = sortOrderPref;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFavoriteItemSelected(Uri movieUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, movieUri);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent movieDetail = new Intent(this, MovieDetailActivity.class).setData(movieUri);
            startActivity(movieDetail);
        }
    }

    @Override
    public void onItemSelected(Bundle bundle) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.movies_parcel), bundle);

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, movieDetailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent movieDetail = new Intent(this, MovieDetailActivity.class).putExtras(bundle);
            startActivity(movieDetail);
        }
    }
}
