package com.ahmedtikiwa.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ahmedtikiwa.popularmovies.R;
import com.ahmedtikiwa.popularmovies.models.MovieTrailer;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed on 2016/10/03.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<MovieTrailer> mData;
    private ViewHolder viewHolder;

    public MovieTrailerAdapter(Context context, ArrayList<MovieTrailer> objects) {
        this.mContext = context;
        this.mData = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_trailer_list_item, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // assign the Movie object to the item position
        final MovieTrailer movieTrailer = mData.get(position);


        Glide.with(mContext)
                .load("http://img.youtube.com/vi/" + movieTrailer.getKey() + "/maxresdefault.jpg")
                .error(R.drawable.ic_image_black_24dp)
                .into(holder.trailer);

        holder.share_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, movieTrailer.getName());
                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + movieTrailer.getKey());
                shareIntent.setType("text/plain");
                view.getContext().startActivity(shareIntent);
            }
        });

        holder.trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playTrailer = new Intent(Intent.ACTION_VIEW);
                playTrailer.setData(Uri.parse("http://www.youtube.com/watch?v=" + movieTrailer.getKey()));
                view.getContext().startActivity(playTrailer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView trailer;
        public ImageView share_trailer;

        public ViewHolder(View itemView) {
            super(itemView);
            trailer = (ImageView) itemView.findViewById(R.id.trailer_item);
            share_trailer = (ImageView) itemView.findViewById(R.id.share_trailer);
        }
    }

}