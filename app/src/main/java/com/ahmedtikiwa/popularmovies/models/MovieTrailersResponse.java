package com.ahmedtikiwa.popularmovies.models;

/**
 * Created by Ahmed on 2016/12/03.
 */

import java.util.ArrayList;

public class MovieTrailersResponse {

    private Integer id;
    private ArrayList<MovieTrailer> results = new ArrayList<MovieTrailer>();

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The results
     */
    public ArrayList<MovieTrailer> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(ArrayList<MovieTrailer> results) {
        this.results = results;
    }

}
