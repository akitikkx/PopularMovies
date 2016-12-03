package com.ahmedtikiwa.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.models.MovieReview;

import java.util.ArrayList;

/**
 * Created by Ahmed on 2016/10/03.
 */

public class MovieReviewAdapter extends ArrayAdapter<MovieReview> {

    private final Context mContext;
    private final int resource;
    private ArrayList<MovieReview> data;
    private ViewHolder viewHolder;

    public MovieReviewAdapter(Context context, int resource, ArrayList<MovieReview> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.resource = resource;
        this.data = objects;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // assign the Movie object to the item position
        final MovieReview movieReview = getItem(position);

        // inflate the layout if the view is null
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_review_list_item, parent, false);
        }

        viewHolder = new MovieReviewAdapter.ViewHolder(convertView);
        viewHolder.author = (TextView) convertView.findViewById(R.id.review_author);
        viewHolder.author_content = (TextView) convertView.findViewById(R.id.review_content);

        if (movieReview != null) {
            viewHolder.author.setText(movieReview.getAuthor());
            viewHolder.author_content.setText(movieReview.getContent());
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView author;
        private TextView author_content;

        private ViewHolder(View rootView) {
            author = (TextView) rootView.findViewById(R.id.review_author);
            author_content = (TextView) rootView.findViewById(R.id.review_content);
        }
    }

}