package com.dorothy.v2ex.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dorothy on 16/9/6.
 */
public class NodeDetail {
    private String created;
    private String footer;
    private String header;
    private long id;
    private String name;
    private String title;
    @SerializedName("title_alternative")
    private String titleAlternative;
    private int topics;
    private String url;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleAlternative() {
        return titleAlternative;
    }

    public void setTitleAlternative(String titleAlternative) {
        this.titleAlternative = titleAlternative;
    }

    public int getTopics() {
        return topics;
    }

    public void setTopics(int topics) {
        this.topics = topics;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
