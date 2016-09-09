package com.dorothy.v2ex.models;

import java.io.Serializable;

/**
 * Created by dorothy on 16/8/5.
 */
public class Topic implements Serializable {
    private long id;
    private String title;
    private String url;
    private String content;
    private String content_rendered;
    private Integer replies;
    private Member member;
    private Node node;
    private Integer created;
    private Integer lastModified;
    private Integer lastTouched;
    private String once;

    public String getOnce() {
        return once;
    }

    public void setOnce(String once) {
        this.once = once;
    }

    /**
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(long id) {
        this.id = id;
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
        return content_rendered;
    }

    /**
     * @param contentRendered The content_rendered
     */
    public void setContentRendered(String contentRendered) {
        this.content_rendered = contentRendered;
    }

    /**
     * @return The replies
     */
    public Integer getReplies() {
        return replies;
    }

    /**
     * @param replies The replies
     */
    public void setReplies(Integer replies) {
        this.replies = replies;
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
     * @return The node
     */
    public Node getNode() {
        return node;
    }

    /**
     * @param node The node
     */
    public void setNode(Node node) {
        this.node = node;
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
     * @return The lastTouched
     */
    public Integer getLastTouched() {
        return lastTouched;
    }

    /**
     * @param lastTouched The last_touched
     */
    public void setLastTouched(Integer lastTouched) {
        this.lastTouched = lastTouched;
    }
}
