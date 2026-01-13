package com.sprint.mission.entity;

public class Message extends BaseEntity {
    private final User user;
    private final Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
        super();
        this.content = getValidatedTrimmedContent(content);
        this.user = user;
        this.channel = channel;
    }

    public void updateContent(String content) {
        this.content = getValidatedTrimmedContent(content);
        touch();
    }

    private String getValidatedTrimmedContent(String content) {
        validateContentIsNotBlank(content);
        return content.trim();
    }

    private void validateContentIsNotBlank(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }
}
