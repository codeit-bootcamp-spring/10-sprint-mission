package com.sprint.mission.discodeit.entity;

public class Message extends Entity {
    private final Channel channel;
    private final User user;
    private String content;

    public Message(Channel channel, User user, String content) {
        super();
        this.channel = channel;
        this.user = user;
        this.content = content;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Message updateMessageContent(String content) {
        // 마지막 수정 시각 갱신
        super.update();
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "Message [id=%s, content=%s, channel=%s, user=%s]",
                getId().toString().substring(0, 5),
                content,
                "[id=" + channel.getId().toString().substring(0, 5) + ", " + channel.getName() + "]",
                "[id=" +  user.getId().toString().substring(0, 5) + ", " + user.getNickname() + "]"
        );
    }
}
