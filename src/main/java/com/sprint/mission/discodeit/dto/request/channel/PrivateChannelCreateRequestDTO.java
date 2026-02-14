package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChannelCreateRequestDTO {
    @NotNull
    private UUID userId;

    private List<UUID> memberIds;
}
