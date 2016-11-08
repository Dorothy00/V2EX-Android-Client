package com.dorothy.v2ex.utils;

import android.text.TextUtils;

import com.dorothy.v2ex.models.Member;
import com.dorothy.v2ex.models.Node;
import com.dorothy.v2ex.models.Notification;
import com.dorothy.v2ex.models.Reply;
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

    public static final int FROM_TAB = 1;    // 从首页进入
    public static final int FROM_NODE = 2;   // 从节点进入

    public static List<Topic> parseTopicList(String htmlStr, int from) {
        List<Topic> topicList = new ArrayList<>();

        Document document = Jsoup.parse(htmlStr);
        Elements elements;
        if (from == FROM_TAB) {
            elements = document.select("div[class~=cell item]");
        } else {
            elements = document.select("div[id=TopicsNode]").select("div[class~=cell]");
        }
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
            if (from == FROM_TAB) {
                String nodeTitleStr = childElement3.getElementsByClass("node").text();
                node.setTitle(nodeTitleStr);
                String nodeNameStr = childElement3.getElementsByClass("node").attr("href");
                node.setName(nodeNameStr.substring("/go/".length()));
            }


            Element childElement4 = childElements.get(3);
            Elements countElements = childElement4.getElementsByClass("count_livid");
            String replyCount = "0";
            if (countElements.size() > 0) {
                replyCount = childElement4.getElementsByClass("count_livid").text();
                if (TextUtils.isEmpty(replyCount)) {
                    replyCount = "0";
                }
            }
            topic.setReplies(Integer.valueOf(replyCount));

            topicList.add(topic);
        }
        return topicList;
    }

    public static String parseCollectUrl(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[id=Main]").select("div[class=header]");
        Elements urlElements = elements.select("div[class~=fr]").select("a");
        return urlElements.attr("href");
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
        if (TextUtils.isEmpty(content)) {
            content = elements.select("div[class=topic_content]").html();
        }
        topic.setContentRendered(content);

        String once = elements.select("form[method=post]").select("input[name=once]").attr("value");
        topic.setOnce(once);

        return topic;
    }

    public static List<Reply> parseReply(String htmlStr) {
        List<Reply> replyList = new ArrayList<>();
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.select("div[id=Main]");

        Elements replyElements = elements.select("div[class=box]").get(1).select("div[id~=^r_]");
        for (Element element : replyElements) {
            Reply reply = new Reply();
            Member member = new Member();
            reply.setMember(member);

            String avatar = element.select("img[src~=^//]").attr("src");
            member.setAvatarNormal(avatar);
            String username = element.select("a[href~=^/member/]").text();
            member.setUsername(username);
            String replyContent = element.select("div[class=reply_content]").html();
            reply.setContentRendered(replyContent);
            String thanks = element.select("span[class=no]").text();
            reply.setThanks(Integer.valueOf(thanks));
            replyList.add(reply);
        }

        return replyList;
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

    public static String parseLoginErrorMsg(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        String errMsg = document.select("div[class=problem]").select("li").text();
        return errMsg;
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

    public static List<Node> parseCollectedNode(String htmlStr) {
        List<Node> nodeList = new ArrayList<>();
        Document document = Jsoup.parse(htmlStr);
        Elements nodeElements = document.select("div[id=MyNodes]").select("a[class=grid_item]");
        for (Element element : nodeElements) {
            Node node = new Node();
            String name = element.attr("href");
            if (name.startsWith("/go/")) {
                node.setName(name.substring("/go/".length()));
            }
            String imgUrl = element.select("img").attr("src");
            node.setImgUrl(imgUrl);
            String[] titleAndNum = element.text().split(" ");
            if (titleAndNum.length < 2)
                continue;
            node.setTitle(titleAndNum[0]);
            node.setTopics(Integer.valueOf(titleAndNum[1]));
            nodeList.add(node);
        }
        return nodeList;
    }

    public static String parseNewTopicOnce(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        String once = document.select("div[id=Main]").select("input[name=once]").attr("value");
        return once;
    }

    public static String parseNewTopicProblem(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        String problemStr = document.select("div[class=problem]").text();
        return problemStr;
    }

    public static boolean isPostNewTopicSuccess(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        if (document.select("div[class=problem]") == null) {
            return true;
        } else {
            return false;
        }
    }

}
