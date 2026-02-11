package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadType;
import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

public interface ReadStatusServiceDTO {
    record ReadStatusCreation(@NonNull UUID userId, @NonNull UUID channelId) {}
    record ReadStatusUpdate(@NonNull UUID id, @NonNull ReadType type) {}
    @Builder
    record ReadStatusResponse(UUID id, UUID userId, UUID channelId, ReadType type) {}
}
