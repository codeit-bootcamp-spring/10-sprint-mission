package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MemberFindRequestDTO {
    @NotNull
    private UUID requesterId;

    @NotNull
    private UUID channelId;
}
