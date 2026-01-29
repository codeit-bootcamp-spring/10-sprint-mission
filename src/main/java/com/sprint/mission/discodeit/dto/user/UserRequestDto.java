package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;

public record UserRequestDto(

    String userName,
    String email,
    String password,
    BinaryContentRequestDto binaryContentDto


) {}



