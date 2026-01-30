package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID channelId;
    private final UUID authorId;
    private String content;
    private List<UUID> attachmentIds; // BinaryContent

    public Message(UUID channelId, UUID authorId, String content, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }

    public String formatForDisplay() {
        // 메시지 출력 전용
        return "[" + channelId.toString().substring(0, 5) + "] "
                + authorId.toString().substring(0, 5) + ": "
                + content
                + attachmentIds;
    }

    public void update(String newContent) {
        this.content = newContent;
        // 업데이트 시간 갱신
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return String.format(
                "Message [id=%s, content=%s, attachmentIds=%s, channel=%s, user=%s]",
                getId().toString().substring(0, 5),
                content,
                attachmentIds,
                "[channelId=" + channelId.toString().substring(0, 5) + "]",
                "[userId=" + authorId.toString().substring(0, 5) + "]"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
