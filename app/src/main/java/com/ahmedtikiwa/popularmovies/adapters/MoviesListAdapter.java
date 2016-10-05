package com.ahmedtikiwa.popularmovies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.ahmedtikiwa.popularmovies.MainActivityFragment.LOG_TAG;

/**
 * Created by Ahmed on 2016/10/03.
 */

public class MoviesListAdapter extends ArrayAdapter<Movie> {

    private final Context mContext;
    private final int resource;
    private ArrayList<Movie> data;
    private ViewHolder viewHolder;

    public MoviesListAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.resource = resource;
        this.data = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movies_list_item, parent, false);
        }

        viewHolder = new ViewHolder(convertView);
        viewHolder.poster = (ImageView)convertView.findViewById(R.id.poster_image);

        if (movie.getPosterPath() != null) {
            String posterUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_RECOMMENDED_SIZE + movie.getPosterPath();
            convertView.setTag(posterUrl);

            Glide.with(mContext)
                    .load(posterUrl)
                    .error(R.drawable.ic_image_black_24dp)
                    .into(viewHolder.poster);
            Log.d(LOG_TAG, posterUrl);
        }



        return convertView;
    }

    private static class ViewHolder {
        private ImageView poster;

        private ViewHolder(View rootView) {
            poster = (ImageView)rootView.findViewById(R.id.poster_image);
        }
    }

}