package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateDto {
    private String name;
    private String email;
    private BinaryContentDto binaryContentDto;
    private String password;
}
