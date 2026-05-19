package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private BinaryContentDto profile;
    private Role role;
    private boolean online;

}
