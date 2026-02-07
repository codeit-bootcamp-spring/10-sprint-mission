package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileCreateRequestDTO(
   byte[] content,

   @NotBlank(message = "contentType이 비어있습니다")
   String contentType
) {}
