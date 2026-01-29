package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;

public record UserRequestDto(

    String username,
    String email,
    String password,
    BinaryContentRequestDto binaryContentDto


) {}



