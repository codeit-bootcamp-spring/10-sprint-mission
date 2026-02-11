package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreation;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface MessageServiceDTO {
    record MessageCreation(@NonNull String content, @NonNull UUID channelId, @NonNull UUID authorId,
                           @Nonnull List<BinaryContentCreation> attachments) {
    }

    record MessageContentUpdate(@NonNull UUID messageId, String newContent, List<UUID> attachmentIds) {
    }

    @Builder
    record MessageResponse(@NonNull UUID id, @NonNull String content, @NonNull UUID channelId, @NonNull UUID authorId,
                           List<UUID> attachmentIds, long createdAt) {
    }
}
