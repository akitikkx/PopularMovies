package com.ahmedtikiwa.popularmovies.activities;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ahmedtikiwa.popularmovies.App;
import com.ahmedtikiwa.popularmovies.R;

/**
 * Created by Ahmed on 2016/10/09.
 */

public class BaseActivity extends AppCompatActivity {

    /**
     * Checks if the device is connected to the internet
     * if no connection, a snackbar is displayed with an option to
     * go to the device settings
     */
    protected void checkNetworkConnection(View view) {
        if (!App.hasNetworkConnection()) {
            Snackbar.make(view, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_no_internet_action_title), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .show();
        }
    }
}
