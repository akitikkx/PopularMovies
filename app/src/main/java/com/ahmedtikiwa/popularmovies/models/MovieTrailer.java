package com.ahmedtikiwa.popularmovies.models;

/**
 * Created by Ahmed on 2016/12/03.
 */

public class MovieTrailer {

    private String id;
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String name;
    private String site;
    private Integer size;
    private String type;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The iso6391
     */
    public String getIso6391() {
        return iso_639_1;
    }

    /**
     * @param iso_639_1 The iso_639_1
     */
    public void setIso6391(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    /**
     * @return The iso31661
     */
    public String getIso31661() {
        return iso_3166_1;
    }

    /**
     * @param iso_3166_1 The iso_3166_1
     */
    public void setIso31661(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The site
     */
    public String getSite() {
        return site;
    }

    /**
     * @param site The site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * @return The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

}
