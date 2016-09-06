package com.dorothy.v2ex.models;

import java.io.Serializable;

/**
 * Created by dorothy on 16/9/2.
 */
public class UserProfile implements Serializable{
    private String username;
    private String avatar;
    private String[] balance;
    private String collectedNodes;
    private String collectTopics;
    private String focusedTopics;
    private String notification;

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
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

    public String getCollectedNodes() {
        return collectedNodes;
    }

    public void setCollectedNodes(String collectedNodes) {
        this.collectedNodes = collectedNodes;
    }

    public String getCollectTopics() {
        return collectTopics;
    }

    public void setCollectTopics(String collectTopics) {
        this.collectTopics = collectTopics;
    }

    public String getFocusedTopics() {
        return focusedTopics;
    }

    public void setFocusedTopics(String focusedTopics) {
        this.focusedTopics = focusedTopics;
    }

    public String[] getBalance() {
        return balance;
    }

    public void setBalance(String[] balance) {
        this.balance = balance;
    }
}
