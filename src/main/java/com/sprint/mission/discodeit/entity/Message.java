package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private User sender;
    private Channel channel;
    private String content;

    public Message(User sender, Channel channel, String content) {
        channel.validateChannelMember(sender);

        this.sender = sender;
        this.channel = channel;
        this.content = content;

        sender.addMessage(this);
        channel.addMessage(this);
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public UUID getSenderId() {
        return sender.getId();
    }

    public UUID getChannelId() {
        return channel.getId();
    }

    public void updateContent(String content) {
        this.content = content;
        markUpdated();
    }

    public void addUser(User user) {
        this.sender = user;

        if (!user.getMessages().contains(this)) {
            user.addMessage(this);
        }
    }

    public void addChannel(Channel channel) {
        this.channel = channel;

        if (!channel.getMessages().contains(this)) {
            channel.addMessage(this);
        }
    }

    public void clear() {
        detachFromUser();
        detachFromChannel();
    }

    public void detachFromChannel() {
        channel.removeMessage(this);
    }

    public void detachFromUser() {
        sender.removeMessage(this);
    }

    public void validateOwner(User user) {
        if (!sender.equals(user)) {
            throw new IllegalArgumentException("메세지의 소유자가 아닙니다. userId: " + user.getId());
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderName=" + sender.getNickName() +
                ", channelName=" + channel.getName() +
                ", content='" + content + '\'' +
                '}';
    }
}
