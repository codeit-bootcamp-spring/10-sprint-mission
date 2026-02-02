package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {
    private String name;
    private String email;
    private String password;
    private BinaryContentCreateDto profileImg;
}
