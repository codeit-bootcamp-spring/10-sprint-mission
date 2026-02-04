package com.sprint.mission.discodeit.dto.request.readStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
public class ReadStatusCreateRequestDTO {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID channelId;

    public ReadStatusCreateRequestDTO(UUID memberId, UUID channelId) {
        this.userId = memberId;
        this.channelId = channelId;
    }
}
