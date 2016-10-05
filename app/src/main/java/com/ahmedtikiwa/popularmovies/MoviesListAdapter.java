package com.ahmedtikiwa.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Ahmed on 2016/10/03.
 */

public class MoviesListAdapter extends ArrayAdapter<MoviesListAdapter.ViewHolder> {


    public MoviesListAdapter(Context context, int resource, List<ViewHolder> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public class ViewHolder {

    }
}
