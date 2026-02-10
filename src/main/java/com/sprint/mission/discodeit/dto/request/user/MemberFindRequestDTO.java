package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MemberFindRequestDTO (
    @NotNull
    UUID requesterId,

    @NotNull
    UUID channelId
) {

}
