package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements  Serializable {
    private static final long serialVersionUID = 1L;

    private User sender;
    private Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
        super(UUID.randomUUID(), System.currentTimeMillis());
        this.channel = channel;
        this.content = content;
        this.addSender(user);
    }

    public void addSender(User user) {
        this.sender = user;
        if (!user.getMessages().contains(this)) {
            user.addMessages(this);
        }
    }

    public void setChannel(Channel channel) {
        this.channel = channel;

        if (!channel.getMessages().contains(this)) {
            channel.addMessage(this);
        }
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel(){
        return channel;
    }

    public UUID getChannelId() {
        return channel.getId();
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "보낸 사람 : " + sender.getName() + ", 내용 : " + content + ", 수정 시간 : " + updatedAt;
    }
}
