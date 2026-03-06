package com.sprint.mission.discodeit.dto.messagedto;

import java.util.UUID;

public record MessageCreateRequestDTO(
    String content,
    UUID channelId,
    UUID authorId
) {

}
