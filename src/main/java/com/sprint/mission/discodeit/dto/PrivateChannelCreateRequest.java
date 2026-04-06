package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PrivateChannelCreateRequest {
    @NotNull(message = "생성자 ID는 필수입니다.")
    private UUID creatorId;

    @NotEmpty(message = "참여자는 최소 1명 이상이어야 합니다.")
    private List<UUID> participantIds;
}
