package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

public interface BinaryContentServiceDTO {
    record BinaryContentCreateRequest(@NonNull String fileName, @NonNull String fileType, @NonNull byte[] data) {
    }

    @Builder
    record BinaryContentResponse(@NonNull UUID id, @NonNull String fileName, @NonNull String fileType,
                                 @NonNull byte[] data) {
    }
}
