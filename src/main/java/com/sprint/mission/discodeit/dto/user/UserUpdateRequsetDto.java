package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import lombok.Getter;

@Getter
public class UserUpdateRequsetDto {
    private String name;
    private String email;
    private BinaryContentCreateDto binaryContentCreateDto;
    private String password;
}
