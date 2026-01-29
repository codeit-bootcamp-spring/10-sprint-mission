package com.sprint.mission.discodeit.dto;

import lombok.NonNull;

import java.util.UUID;

public interface MessageServiceDTO {
    record MessageCreation(@NonNull String content, @NonNull UUID channelId, @NonNull UUID authorId) {}
    record MessageContentUpdate(@NonNull UUID messageId, String newContent) {}
}
