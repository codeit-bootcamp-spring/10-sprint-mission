package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {
    private String name;
    private String email;
    private String password;
    private BinaryContentCreateDto profileImg;
}
