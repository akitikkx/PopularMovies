package com.ahmedtikiwa.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ahmedtikiwa.popularmovies.R;

public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_default_movies_sort_order));
    }

    public static boolean userPrefersFavoriteMovies(Context context) {
        String sortOrderSetting = getPreferredSortOrder(context);
        if (sortOrderSetting.equals(context.getString(R.string.pref_sort_order_favorites_value))) {
            return true;
        }
        return false;
    }

    public static boolean userPrefersPopularMovies(Context context) {
        String sortOrderSetting = getPreferredSortOrder(context);
        if (sortOrderSetting.equals(context.getString(R.string.pref_default_movies_sort_order))) {
            return true;
        }
        return false;
    }
}
