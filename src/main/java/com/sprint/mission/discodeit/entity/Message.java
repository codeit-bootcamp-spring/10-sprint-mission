package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    private String content;
    private Channel channel;
    private User user;

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void updateContent(String newContent){
        this.content = newContent;
        super.setUpdatedAt(System.currentTimeMillis());
    }

    public Message(String content, Channel channel, User user) {
        this.content = content;
        this.channel = channel;
        this.user = user;
    }


}
