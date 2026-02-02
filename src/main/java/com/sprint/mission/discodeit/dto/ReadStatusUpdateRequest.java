package com.sprint.mission.discodeit.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatusUpdateRequest {
    private final UUID id;

    public ReadStatusUpdateRequest(UUID id) {
        this.id = id;
    }
}
