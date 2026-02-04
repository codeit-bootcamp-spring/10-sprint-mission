package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class Message implements Serializable, Comparable<Message> {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    private long updatedAt = createdAt;
    //
    @NonNull
    private String content;
    @NonNull
    private UUID channelId;
    @NonNull
    private UUID authorId;
    // BinaryContent id list
    @ToString.Exclude
    private final Set<UUID> attachmentIds = new HashSet<>();

    public boolean isAuthor(UUID userId) {
        return this.authorId.equals(userId);
    }

    public boolean isInChannel(UUID channelId) {
        return this.channelId.equals(channelId);
    }

    public void update(String newContent, List<UUID> attachments) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (attachments != null && !attachments.isEmpty()) {
            this.attachmentIds.addAll(attachments);
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
