package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageResponse;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable, Comparable<Message> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id;
    private final Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private String content;
    private final UUID channelId;
    private final UUID authorId;
    private final Set<UUID> attachmentIds = new HashSet<>();

    public Message(UUID id, String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = id;
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds.addAll(attachmentIds);
    }

    public boolean isAuthor(UUID userId) {
        return this.authorId.equals(userId);
    }

    public boolean isInChannel(UUID channelId) {
        return this.channelId.equals(channelId);
    }

    public void update(String newContent, List<UUID> attachmentIds) {
        boolean hasUpdated = false;
        hasUpdated |= updateIfChanged(this.content, newContent, val -> this.content = newContent);
        hasUpdated |= addAttachments(attachmentIds);

        if (hasUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public int compareTo(Message m) {
        return createdAt.compareTo(m.createdAt);
    }

    public MessageResponse toResponse() {
        return MessageResponse.builder()
                .id(id)
                .content(content)
                .channelId(channelId)
                .authorId(authorId)
                .attachmentIds(List.copyOf(attachmentIds))
                .createdAt(TimeConverter.toDateTime(createdAt))
                .updatedAt(TimeConverter.toDateTime(updatedAt))
                .build();
    }

    private boolean addAttachments(List<UUID> attachmentIds) {
        if (attachmentIds.isEmpty()) {
            return false;
        }
        this.attachmentIds.addAll(attachmentIds);
        return true;
    }
}
