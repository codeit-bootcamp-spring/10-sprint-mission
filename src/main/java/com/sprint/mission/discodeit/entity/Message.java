package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends CommonEntity{
    private final Channel channel;
    private final User user;
    private String content;


    public Message(String content, Channel channel, User user) {
        this.content = content;
        this.channel = channel;
        this.user = user;
    }

    public void updateContent(String content) {
        this.content = content;
        update();
    }

    public String getMessageStatus() {
        return "메세지: " + content + ", 채널 아이디: " + channel.getId() + ", 유저 아이디: " + user.getId();
    }
}
