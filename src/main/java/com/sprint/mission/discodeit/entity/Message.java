package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageResponse;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ToString
public class Message implements Serializable, Comparable<Message> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    //
    private String content;
    private final UUID channelId;
    private final UUID authorId;
    // BinaryContent id list
    @ToString.Exclude
    private final Set<UUID> attachmentIds = new HashSet<>();

    public Message(@Nonnull String content, @Nonnull UUID channelId, @Nonnull UUID authorId,
                   @Nonnull List<UUID> attachmentIds) {
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
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            this.attachmentIds.addAll(attachmentIds);
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }

    @Override
    public int compareTo(Message o) {
        return Long.compare(this.createdAt, o.createdAt);
    }

    public MessageResponse toResponse() {
        return MessageResponse.builder()
                .id(id)
                .content(content)
                .channelId(channelId)
                .authorId(authorId)
                .attachmentIds(List.copyOf(attachmentIds))
                .createdAt(createdAt)
                .build();
    }
}
