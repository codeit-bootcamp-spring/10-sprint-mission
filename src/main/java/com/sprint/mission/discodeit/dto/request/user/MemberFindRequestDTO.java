package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberFindRequestDTO {
    @NotNull
    private UUID requesterId;

    @NotNull
    private UUID channelId;
}
