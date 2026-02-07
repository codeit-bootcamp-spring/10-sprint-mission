package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PublicCreateRequestDTO(
   @NotBlank(message = "PUBLIC 채널의 channelName이 blank일 수 없습니다")
   String channelName,
   @NotBlank(message = "PUBLIC 채널의 description이 blank일 수 없습니다")
   String description
) {}
