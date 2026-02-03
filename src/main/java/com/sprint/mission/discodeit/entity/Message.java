package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private final UUID authorId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;

    public Message(UUID channelId, UUID authorId, String content, List<UUID> attachmentIds) {
        super();
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds != null ? attachmentIds : new ArrayList<>();
    }
    public Message(UUID channelId, UUID authorId, String content) {
        this(channelId, authorId, content, null);
    }
    public void updateContent(String content) {
        this.content = content;
        updateTimestamps();
    }

    public void updateAttachments(List<UUID> attachmentIds) {
        this.attachmentIds.clear();
        if (attachmentIds != null) {
            this.attachmentIds.addAll(attachmentIds);
        }
        updateTimestamps();
    }

    @Override
    public String toString() {
        return "메시지[채널ID: " + channelId +
                ", 작성자ID: " + authorId +
                ", 내용: " + content + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return java.util.Objects.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}