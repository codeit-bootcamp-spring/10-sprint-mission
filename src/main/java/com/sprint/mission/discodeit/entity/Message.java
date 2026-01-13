package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private User sender;
    private Channel channel;
    private String content;

    public Message(User sender, Channel channel, String content) {
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

    public void updateContent(String content) {
        this.content = content;
        markUpdated();
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
