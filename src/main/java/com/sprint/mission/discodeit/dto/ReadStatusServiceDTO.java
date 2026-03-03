package com.sprint.mission.discodeit.dto;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReadStatusServiceDTO {
    record ReadStatusCreateRequest(@Nonnull UUID userId,
                                   @Nonnull UUID channelId,
                                   @Nonnull @DateTimeFormat LocalDateTime lastReadAt) {
    }

    record ReadStatusUpdateRequest(@Nonnull @DateTimeFormat LocalDateTime newLastReadAt) {
    }

    record ReadStatusUpdateCommand(UUID id, LocalDateTime datetime) {
    }

    // todo: error log
    @Builder
    record ReadStatusResponse(UUID id, UUID userId, UUID channelId,
                              LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastReadAt) {
    }
}
