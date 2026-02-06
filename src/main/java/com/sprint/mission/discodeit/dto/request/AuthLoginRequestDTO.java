package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDTO(
   @NotBlank(message = "username이 blank일 수 없습니다")
   String username,
   @NotBlank(message = "password가 blank일 수 없습니다")
   String password
) {}
