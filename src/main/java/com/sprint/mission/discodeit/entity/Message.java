package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@JsonPropertyOrder({"id", "createdAt", "updatedAt", "content", "channelId", "authorId",
        "attachmentIds"})
public class Message extends BaseEntity {

    private final UUID authorId;
    private final UUID channelId;
    private String content;
    private List<UUID> attachmentIds;

    public Message(UUID authorId, UUID channelId, String content) {
        this.authorId = authorId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = new ArrayList<>();
    }

    public void updateAttachments(List<UUID> attachmentIds) {
        this.attachmentIds = new ArrayList<>(attachmentIds);
        markUpdated();
    }

    public void updateContent(String content) {
        Optional.ofNullable(content)
                .ifPresent(value -> this.content = value);
        markUpdated();
    }

    public void validateSender(UUID userId) {
        if (!authorId.equals(userId)) {
            throw new ApiException(ErrorCode.MESSAGE_SENDER_MISMATCH,
                    "메세지의 sender가 아닙니다. userId: " + userId);
        }
    }
}
