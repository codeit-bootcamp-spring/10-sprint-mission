package com.sprint.mission.entity;

import java.io.Serializable;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public void validateMessageOwner(User user) {
        if (!this.user.equals(user)) {
            throw new IllegalArgumentException("해당 메시지에 대한 권한이 없습니다.");
        }
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
