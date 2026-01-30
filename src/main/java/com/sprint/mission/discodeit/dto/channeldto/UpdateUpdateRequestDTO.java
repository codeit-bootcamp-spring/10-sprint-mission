package com.sprint.mission.discodeit.dto.channeldto;

import java.util.UUID;

public record UpdateUpdateRequestDTO(
        UUID channelID,
        String newName,
        String newDescription
) {

}
