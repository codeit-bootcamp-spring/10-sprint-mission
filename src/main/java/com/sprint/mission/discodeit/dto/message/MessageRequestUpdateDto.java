package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageRequestUpdateDto(UUID id,
                                      String content) {
}
