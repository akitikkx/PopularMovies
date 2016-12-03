package com.ahmedtikiwa.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.models.MovieTrailer;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed on 2016/10/03.
 */

public class MovieTrailerAdapter extends ArrayAdapter<MovieTrailer> {

    private final Context mContext;
    private final int resource;
    private ArrayList<MovieTrailer> data;
    private ViewHolder viewHolder;

    public MovieTrailerAdapter(Context context, int resource, ArrayList<MovieTrailer> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.resource = resource;
        this.data = objects;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // assign the Movie object to the item position
        final MovieTrailer movieTrailer = getItem(position);

        // inflate the layout if the view is null
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailer_list_item, parent, false);
        }

        viewHolder = new MovieTrailerAdapter.ViewHolder(convertView);
        viewHolder.trailer = (ImageView) convertView.findViewById(R.id.trailer_item);

        if (movieTrailer != null) {
            Glide.with(mContext)
                    .load("http://img.youtube.com/vi/" + movieTrailer.getKey() + "/maxresdefault.jpg")
                    .error(R.drawable.ic_image_black_24dp)
                    .into(viewHolder.trailer);

            viewHolder.trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent playTrailer = new Intent(Intent.ACTION_VIEW);
                    playTrailer.setData(Uri.parse("http://www.youtube.com/watch?v=" + movieTrailer.getKey()));
                    view.getContext().startActivity(playTrailer);
                }
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        private ImageView trailer;
        private TextView trailer_name;

        private ViewHolder(View rootView) {
            trailer = (ImageView) rootView.findViewById(R.id.trailer_item);
        }
    }

}