package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Private Channel 생성 정보")
public record PrivateChannelCreateRequestDTO(
    List<UUID> participantIds
) {}
