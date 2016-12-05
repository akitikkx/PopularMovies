package com.ahmedtikiwa.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.fragments.MovieFragment;
import com.ahmedtikiwa.popularmovies.utils.Constants;
import com.bumptech.glide.Glide;

/**
 * Created by Ahmed on 2016/10/03.
 */

public class FavoriteListAdapter extends CursorAdapter {

    private ViewHolder viewHolder;

    public FavoriteListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movies_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // load the poster image into the imageview if the server returns the value
        if (cursor.getString(MovieFragment.COLUMN_POSTER_PATH) != null) {
            String posterUrl = Constants.TMDB_IMAGE_BASE_URL + Constants.TMDB_IMAGE_RECOMMENDED_SIZE + cursor.getString(MovieFragment.COLUMN_POSTER_PATH);

            Glide.with(mContext)
                    .load(posterUrl)
                    .error(R.drawable.ic_image_black_24dp)
                    .into(viewHolder.poster);
        }

    }

    private static class ViewHolder {
        private ImageView poster;

        private ViewHolder(View rootView) {
            poster = (ImageView) rootView.findViewById(R.id.poster_image);
        }
    }

}
