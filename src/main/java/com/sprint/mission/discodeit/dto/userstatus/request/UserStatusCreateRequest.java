package com.sprint.mission.discodeit.dto.userstatus.request;

import lombok.NonNull;

import java.util.UUID;

public record UserStatusCreateRequest(@NonNull UUID userId) {
}
