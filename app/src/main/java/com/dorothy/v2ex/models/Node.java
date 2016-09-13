package com.dorothy.v2ex.models;

import java.io.Serializable;

/**
 * Created by dorothy on 16/8/5.
 */
public class Node implements Serializable {

    private Integer id;
    private String name;
    private String title;
    private String titleAlternative;
    private String url;
    private Integer topics;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String imgUrl;

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
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The titleAlternative
     */
    public String getTitleAlternative() {
        return titleAlternative;
    }

    /**
     * @param titleAlternative The title_alternative
     */
    public void setTitleAlternative(String titleAlternative) {
        this.titleAlternative = titleAlternative;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The topics
     */
    public Integer getTopics() {
        return topics;
    }

    /**
     * @param topics The topics
     */
    public void setTopics(Integer topics) {
        this.topics = topics;
    }


}

