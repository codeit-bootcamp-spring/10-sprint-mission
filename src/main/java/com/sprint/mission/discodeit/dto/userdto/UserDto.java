package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.enums.Role;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    boolean online,
    Role role
) {

}
