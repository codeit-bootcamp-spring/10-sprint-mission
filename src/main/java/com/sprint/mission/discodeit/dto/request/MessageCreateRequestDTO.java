package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequestDTO(
   @NotBlank(message = "메시지 content가 blank일 수 없습니다")
   String content,
   @NotBlank(message = "channelId가 blank일 수 없습니다")
   UUID channelId,
   @NotBlank(message = "authorId가 blank일 수 없습니다")
   UUID authorId,
   @NotNull(message = "attachments가 empty일 수 있으나 null일 수 없습니다")
   List<AttachmentCreateRequestDTO> attachments
) {}
