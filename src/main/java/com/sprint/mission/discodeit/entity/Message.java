package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private String content;
    private final User user;
    private final Channel channel;
    private boolean isEdited; // 수정 여부
    private boolean isPinned; // 고정 여부

    public Message(String content, User user, Channel channel) {
        super();
        this.content = content;
        this.user = user;
        this.channel = channel;
        this.isEdited = false;
        this.isPinned = false;
    }

    public void update(String content) {
        this.content = content;
        this.isEdited = true;
        this.updated();
    }

    public void togglePin() { // 메시지 고정/해제
        this.isPinned = !this.isPinned;
    }

    public String getContent() { return content; }
    public User getUser() { return user; }
    public Channel getChannel() { return channel; }
    public boolean isEdited() { return isEdited; }
    public boolean isPinned() { return isPinned; }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isEdited=" + isEdited +
                ", isPinned=" + isPinned +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
