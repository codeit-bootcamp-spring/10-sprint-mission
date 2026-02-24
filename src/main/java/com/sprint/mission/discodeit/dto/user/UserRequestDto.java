package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;

import java.util.UUID;

public record UserRequestDto(
    UUID userId,
    String userName,
    String email,
    String password,
    BinaryContentRequestDto binaryContentDto


) {}



