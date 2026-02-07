package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MessageUpdateRequestDTO(
   @NotBlank(message = "newContent가 blank일 수 없습니다")
   String newContent,
   @NotNull(message = "attachments가 empty일 수 있으나 null일 수 없습니다")
   List<AttachmentCreateRequestDTO> attachments
) {}
