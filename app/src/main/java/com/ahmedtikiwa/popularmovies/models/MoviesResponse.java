package com.ahmedtikiwa.popularmovies.models;

/**
 * Created by Ahmed on 2016/10/04.
 */

import java.util.ArrayList;

public class MoviesResponse {

    private Integer page;
    private ArrayList<Movie> results = new ArrayList<Movie>();
    private Integer total_results;
    private Integer total_pages;

    /**
     * @return The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return The results
     */
    public ArrayList<Movie> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(ArrayList<Movie> results) {
        this.results = results;
    }

    /**
     * @return The totalResults
     */
    public Integer getTotalResults() {
        return total_results;
    }

    /**
     * @param total_results The total_results
     */
    public void setTotalResults(Integer total_results) {
        this.total_results = total_results;
    }

    /**
     * @return The total_pages
     */
    public Integer getTotalPages() {
        return total_pages;
    }

    /**
     * @param totalPages The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.total_pages = total_pages;
    }

}