package com.sprint.mission.discodeit.dto.userstatus.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record UserStatusUpdateRequest(@JsonProperty("newLastActiveAt") @DateTimeFormat LocalDateTime datetime) {
}
