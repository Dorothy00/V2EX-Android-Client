package com.dorothy.v2ex.models;

/**
 * Created by dorothy on 16/8/14.
 */

import com.google.gson.annotations.SerializedName;

public class Reply {

    private String content;

    @SerializedName("content_rendered")
    private String contentRendered;

    private Integer created;

    private Integer id;
    @SerializedName("last_modified")
    private Integer lastModified;

    private Member member;

    private Integer thanks;

    /**
     * @return The content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return The contentRendered
     */
    public String getContentRendered() {
        return contentRendered;
    }

    /**
     * @param contentRendered The content_rendered
     */
    public void setContentRendered(String contentRendered) {
        this.contentRendered = contentRendered;
    }

    /**
     * @return The created
     */
    public Integer getCreated() {
        return created;
    }

    /**
     * @param created The created
     */
    public void setCreated(Integer created) {
        this.created = created;
    }

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
     * @return The lastModified
     */
    public Integer getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified The last_modified
     */
    public void setLastModified(Integer lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return The member
     */
    public Member getMember() {
        return member;
    }

    /**
     * @param member The member
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * @return The thanks
     */
    public Integer getThanks() {
        return thanks;
    }

    /**
     * @param thanks The thanks
     */
    public void setThanks(Integer thanks) {
        this.thanks = thanks;
    }

}
