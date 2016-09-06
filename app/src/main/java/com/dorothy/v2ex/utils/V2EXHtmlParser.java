package com.dorothy.v2ex.utils;

import android.text.TextUtils;
import android.util.Log;

import com.dorothy.v2ex.models.Member;
import com.dorothy.v2ex.models.Node;
import com.dorothy.v2ex.models.Notification;
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

    public static Topic parseTopic(String htmlStr) {
        Topic topic = new Topic();
        Member member = new Member();
        Node node = new Node();
        topic.setMember(member);
        topic.setNode(node);
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[id=Main]");
        Elements headerElements = elements.select("div[class=header]");
        String avatar = headerElements.select("a[href~=^/member/]").select("img").attr("src");
        member.setAvatarLarge(avatar);
        String nameStr = headerElements.select("a[href~=^/member/]").first().attr("href");
        member.setUsername(nameStr.substring("/member/".length()));
        String nodestr = headerElements.select("a[href~=^/go/]").attr("href");
        node.setName(nodestr.substring("/go/".length()));
        String title = headerElements.select("h1").text();
        topic.setTitle(title);

        Elements contentElements = elements.select("div[class=topic_content]").select
                ("div[class=markdown_body]");
        String content = contentElements.html();
        topic.setContentRendered(content);

        return topic;
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
            userProfile.setCollectedNodes(node);
            String topic = element2.select("a[href=/my/topics]").select("span[class=bigger]")
                    .text();
            userProfile.setCollectTopics(topic);
            String following = element2.select("a[href=/my/following").select
                    ("span[class=bigger]").text();
            userProfile.setFocusedTopics(following);
        }
        String notifications = element.select("a[href=/notifications]").text();
        int end = notifications.indexOf(" 条未读提醒");
        userProfile.setNotification(notifications.substring(0, end));

        String money = element.select("div[id=money]").select("a").text();
        String[] balance = money.split(" ");
        userProfile.setBalance(balance);

        return userProfile;
    }

    public static List<Notification> parseNotification(String htmlStr) {
        List<Notification> notifications = new ArrayList<>();
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[id=Main]").select("div[class=box]").first()
                .select("div[class=cell]");
        for (Element element : elements) {
            Notification notification = new Notification();
            Member member = new Member();
            notification.setMember(member);

            Elements tdElements = element.select("td");
            if (tdElements.size() < 2)
                continue;

            Element element1 = tdElements.get(0);
            String avatar = element1.select("img").attr("src");
            member.setAvatarMini(avatar);
            String usernameStr = element1.select("a").attr("href");
            if (usernameStr.startsWith("/member/")) {
                int start = "/member/".length();
                member.setUsername(usernameStr.substring(start));
            }

            Element element2 = tdElements.get(1);
            String title = element2.select("span").first().text();
            notification.setReplyTitle(title);
            String time = element2.select("span").get(1).text();
            notification.setTime(time);
            String replyContent = element2.select("div[class=payload]").text();
            notification.setReplyContent(replyContent);
            String idStr = element2.select("a[href~=^/t/]").attr("href");
            if (!TextUtils.isEmpty(idStr)) {
                int start = "/t/".length();
                int end = idStr.indexOf("#");
                String id = idStr.substring(start, end);
                notification.setTopicId(Long.valueOf(id));
            }

            Elements memberElements = element2.select("div[class=payload]").select("a");
            List<String> members = new ArrayList<>();
            for (Element memberElement : memberElements) {
                String memberStr = memberElement.text();
                members.add(memberStr);
            }
            notification.setMentionedMember(members);
            notifications.add(notification);

        }
        return notifications;
    }

}
