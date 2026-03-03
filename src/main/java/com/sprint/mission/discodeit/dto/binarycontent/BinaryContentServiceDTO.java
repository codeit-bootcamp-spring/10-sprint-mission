package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BinaryContentServiceDTO {

    @Builder
    record BinaryContentResponse(@NonNull UUID id, @NonNull String fileName, @NonNull byte[] data,
                                 LocalDateTime createdAt) {
    }
}
