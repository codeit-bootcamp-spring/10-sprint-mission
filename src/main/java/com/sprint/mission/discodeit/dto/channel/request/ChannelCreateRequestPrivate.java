package com.sprint.mission.discodeit.dto.channel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Private Channel 생성 정보")
public record ChannelCreateRequestPrivate(

        @NotNull(message = "참여자 목록은 필수입니다.")
        @NotEmpty(message = "참여자는 최소 1명 이상입니다.")
        @Schema(
                description = "Private Channel 참여자 Id 목록",
                example = "[\\\"6f7e8ac7-6b84-4b29-8fc5-1b66b3bfdd11\\\", \\\"28ea0814-09b2-4cb5-833a-8c886dc487cb\\\"]"
        )
        List<UUID> participantIds
) {}
