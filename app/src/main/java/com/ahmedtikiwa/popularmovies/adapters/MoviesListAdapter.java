package com.ahmedtikiwa.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.activities.MovieDetailActivity;
import com.ahmedtikiwa.popularmovies.models.Movie;
import com.ahmedtikiwa.popularmovies.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        // assign the Movie object to the item position
        final Movie movie = getItem(position);

        // inflate the layout if the view is null
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movies_list_item, parent, false);
        }

        viewHolder = new ViewHolder(convertView);
        viewHolder.poster = (ImageView)convertView.findViewById(R.id.poster_image);

        // load the poster image into the imageview if the server returns the value
        if (movie.getPosterPath() != null) {
            String posterUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_RECOMMENDED_SIZE + movie.getPosterPath();
            convertView.setTag(posterUrl);

            Glide.with(mContext)
                    .load(posterUrl)
                    .error(R.drawable.ic_image_black_24dp)
                    .into(viewHolder.poster);
        }

        // handle the onclick of the movie poster from the gridview
        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(mContext.getString(R.string.movies_parcel), data.get(position));

                Intent viewMovieDetails = new Intent(mContext, MovieDetailActivity.class);
                viewMovieDetails.putExtras(bundle);
                mContext.startActivity(viewMovieDetails);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private ImageView poster;

        private ViewHolder(View rootView) {
            poster = (ImageView)rootView.findViewById(R.id.poster_image);
        }
    }

}
