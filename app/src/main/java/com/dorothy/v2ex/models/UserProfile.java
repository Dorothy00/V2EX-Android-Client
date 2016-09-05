package com.dorothy.v2ex.models;

import java.io.Serializable;

/**
 * Created by dorothy on 16/9/2.
 */
public class UserProfile implements Serializable{
    private String username;
    private String avatar;
    private int collectedNodes;
    private int collectTopics;
    private int focusedTopics;
    private int notification;

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getCollectedNodes() {
        return collectedNodes;
    }

    public void setCollectedNodes(int collectedNodes) {
        this.collectedNodes = collectedNodes;
    }

    public int getCollectTopics() {
        return collectTopics;
    }

    public void setCollectTopics(int collectTopics) {
        this.collectTopics = collectTopics;
    }

    public int getFocusedTopics() {
        return focusedTopics;
    }

    public void setFocusedTopics(int focusedTopics) {
        this.focusedTopics = focusedTopics;
    }
}
