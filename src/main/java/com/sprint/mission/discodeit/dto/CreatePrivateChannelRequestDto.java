package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Schema(name = "PrivateChannelCreateRequest", description = "Private Channel 생성 정보")
public record CreatePrivateChannelRequestDto(
    @NotEmpty(message = "프라이빗 채널 생성 시 최소 1명 이상의 참여자가 포함되어야 합니다.")
    List<UUID> participantIds
) {
}
