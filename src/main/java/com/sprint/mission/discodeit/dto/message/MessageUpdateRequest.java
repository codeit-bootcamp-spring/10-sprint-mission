package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;

public record MessageUpdateRequest(
    @JsonIgnore
    UUID newMessageId,
    String newContent
) {

}
