package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 Channel 정보")
public record ChannelUpdateRequestDTO(
    String newName,
    String newDescription
) {}
