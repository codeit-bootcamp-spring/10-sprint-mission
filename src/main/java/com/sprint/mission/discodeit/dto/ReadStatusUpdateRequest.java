package com.sprint.mission.discodeit.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadStatusUpdateRequest {
    private UUID id;

    public ReadStatusUpdateRequest(UUID id) {
        this.id = id;
    }
}
