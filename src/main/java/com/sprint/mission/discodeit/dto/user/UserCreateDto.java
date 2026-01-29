package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreateDto {
    private String name;
    private String email;
    private String password;
    private BinaryContentDto profileImg;
}
