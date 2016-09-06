package com.dorothy.v2ex.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dorothy on 16/9/5.
 */
public class Notification implements Serializable {
    private Member member;
    private long topicId;
    private List<String> mentionedMember;
    private String replyTitle;
    private String replyContent;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public List<String> getMentionedMember() {
        return mentionedMember;
    }

    public void setMentionedMember(List<String> mentionedMember) {
        this.mentionedMember = mentionedMember;
    }

    public String getReplyTitle() {
        return replyTitle;
    }

    public void setReplyTitle(String replyTitle) {
        this.replyTitle = replyTitle;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

}
