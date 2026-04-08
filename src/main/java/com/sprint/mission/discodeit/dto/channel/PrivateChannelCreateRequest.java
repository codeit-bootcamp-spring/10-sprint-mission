package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Private Channel 생성 정보")
public record PrivateChannelCreateRequest(
        @Schema(
                description = "Private Channel 참여자 ID 목록",
                example = "[\"6f7e8ac7-6b84-4b29-8fc5-1b66b3bfdd11\", \"28ea0814-09b2-4cb5-833a-8c886dc487cb\"]"
        )
        @NotEmpty(message = "participantIds는 비어 있을 수 없습니다.")
        Set<@NotNull(message = "participantIds에는 null이 포함될 수 없습니다.") UUID> participantIds
) {
}
