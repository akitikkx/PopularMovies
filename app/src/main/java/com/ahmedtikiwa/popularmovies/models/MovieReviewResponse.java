package com.ahmedtikiwa.popularmovies.models;

/**
 * Created by Ahmed on 2016/12/02.
 */

import java.util.ArrayList;

public class MovieReviewResponse {

    private Integer id;
    private Integer page;
    private ArrayList<MovieReview> results = new ArrayList<MovieReview>();
    private Integer total_pages;
    private Integer total_results;

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
    public ArrayList<MovieReview> getResults() {
        return results;
    }

    /**
     * @param results The results
     */
    public void setResults(ArrayList<MovieReview> results) {
        this.results = results;
    }

    /**
     * @return The total_pages
     */
    public Integer getTotalPages() {
        return total_pages;
    }

    /**
     * @param total_pages The total_pages
     */
    public void setTotalPages(Integer total_pages) {
        this.total_pages = total_pages;
    }

    /**
     * @return The total_results
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

}
