package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private final User user;
    private final Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
        if (user == null) {
            throw new IllegalArgumentException("user는 null일 수 없습니다.");
        }
        if (channel == null) {
            throw new IllegalArgumentException("channel은 null일 수 없습니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 비어 있을 수 없습니다.");
        }
        this.user = user;
        this.channel = channel;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + getId() +
                ", user=" + user +
                ", channel=" + channel +
                ", content='" + content + '\'' +
                '}';
    }
    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
