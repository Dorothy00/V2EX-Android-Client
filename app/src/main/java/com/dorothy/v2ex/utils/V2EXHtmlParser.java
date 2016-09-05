package com.dorothy.v2ex.utils;

import android.text.TextUtils;

import com.dorothy.v2ex.models.Member;
import com.dorothy.v2ex.models.Node;
import com.dorothy.v2ex.models.Topic;
import com.dorothy.v2ex.models.UserProfile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorothy on 16/8/29.
 */
public class V2EXHtmlParser {

    public static List<Topic> parseTopicList(String htmlStr) {
        List<Topic> topicList = new ArrayList<Topic>();

        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[class~=cell item]");
        for (Element element : elements) {
            Topic topic = new Topic();
            Member member = new Member();
            Node node = new Node();
            topic.setMember(member);
            topic.setNode(node);

            Elements childElements = element.select("table").select("tr").select("td");

            Element childElement1 = childElements.get(0);
            String avatar = childElement1.getElementsByTag("img").attr("src");
            member.setAvatarNormal(avatar);
            String userLink = childElement1.getElementsByTag("a").attr("href");
            if (userLink != null && userLink.startsWith("/member/")) {
                member.setUsername(userLink.substring("/member/".length()));
            }

            Element childElement3 = childElements.get(2);

            String title = childElement3.select("a[href~=^/t/]").text();
            topic.setTitle(title);
            String topicLink = childElement3.getElementsByTag("a").attr("href");
            if (topicLink != null && topicLink.startsWith("/t/")) {
                int start = "/t/".length();
                int end = topicLink.indexOf("#");
                topic.setId(Integer.valueOf(topicLink.substring(start, end)));
            }
            String nodeStr = childElement3.getElementsByClass("node").text();
            node.setName(nodeStr);

            Element childElement4 = childElements.get(3);
            String replyCount = childElement4.getElementsByClass("count_livid").text();
            if (TextUtils.isEmpty(replyCount)) {
                replyCount = "0";
            }
            topic.setReplies(Integer.valueOf(replyCount));

            topicList.add(topic);

        }
        return topicList;
    }

    public static String parseTopicContent(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[class=markdown_body]");
        if (elements.size() == 0) {
            elements = document.select("div[class=topic_content]");
        }
        String content = elements.html();
        return content;
    }

    public static String[] parseLoginField(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("form[method=post]").select("tr");
        Element usernameElement = elements.get(0);
        String usernameField = usernameElement.getElementsByTag("input").attr("name");

        Element passwordElement = elements.get(1);
        String passwordField = passwordElement.select("input[type=password]").attr("name");
        Element onceElement = elements.get(2);
        String onceField = onceElement.select("input[name=once]").attr("value");
        return new String[]{usernameField, passwordField, onceField};
    }

    public static boolean isLoginSuccess(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[class=problem]");
        if (elements.size() > 0)
            return false;
        return true;
    }

    public static UserProfile parseUserProfile(String htmlStr) {
        UserProfile userProfile = new UserProfile();
        Document document = Jsoup.parse(htmlStr);
        Element element = document.select("div[id=Rightbar]").select("div[class=box]").first();
        Elements elementsTables = element.select("table");
        if (elementsTables.size() > 1) {
            Element element1 = elementsTables.get(0);
            Element elementAvatar = element1.getElementsByTag("img").first();
            String avatar = elementAvatar.attr("src");
            userProfile.setAvatar(avatar);

            Element elementUsername = element1.select("a[href~=^/member]").first();
            String username = elementUsername.select("a").attr("href").substring("/member/"
                    .length());
            userProfile.setUsername(username);

            Element element2 = elementsTables.get(1);
            String node = element2.select("a[href=/my/nodes]").select("span[class=bigger]").text();
            userProfile.setCollectedNodes(Integer.valueOf(node));
            String topic = element2.select("a[href=/my/topics]").select("span[class=bigger]")
                    .text();
            userProfile.setCollectTopics(Integer.valueOf(topic));
            String following = element2.select("a[href=/my/following").select
                    ("span[class=bigger]").text();
            userProfile.setFocusedTopics(Integer.valueOf(following));
        }
        String notifications = element.select("a[href=/notifications]").text();
        int end = notifications.indexOf(" 条未读提醒");
        userProfile.setNotification(Integer.valueOf(notifications.substring(0, end)));

        return userProfile;
    }

}
