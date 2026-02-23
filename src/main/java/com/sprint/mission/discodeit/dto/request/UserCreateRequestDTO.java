package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User 생성 정보")
public record UserCreateRequestDTO(
    String username,

    String email,

    String password
) {}
