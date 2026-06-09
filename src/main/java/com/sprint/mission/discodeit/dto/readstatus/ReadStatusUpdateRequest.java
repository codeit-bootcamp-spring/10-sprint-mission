package com.sprint.mission.discodeit.dto.readstatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "수정할 읽음 상태 정보")
public record ReadStatusUpdateRequest(
    @JsonProperty("newLastReadAt")
    Instant lastReadAt,

    Boolean newNotificationEnabled
) {

}
