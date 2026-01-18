package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity{
    private String content;
    private final User user;
    private final Channel channel;

    public Message(Channel channel, User user, String content) {
        super();
        this.content = content;
        this.user = user;
        this.channel = channel;
    }
    public String getContent() { return content; }
    public User getUser() { return user; }
    public Channel getChannel() { return channel; }
    public void updateContent(String content) {
        this.content = content;
        updateTimestamps();
    }
    @Override
    public String toString() {
        return "메시지[채널: " + channel.getName() +
                ", 작성자: " + user.getName() +
                ", 내용: " + content +"]";
    }
}
