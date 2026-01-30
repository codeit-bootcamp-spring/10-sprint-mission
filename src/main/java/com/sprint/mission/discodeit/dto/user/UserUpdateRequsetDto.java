package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import lombok.Getter;

@Getter
public class UserUpdateRequsetDto {
    private String name;
    private String email;
    private BinaryContentDto binaryContentDto;
    private String password;
}
