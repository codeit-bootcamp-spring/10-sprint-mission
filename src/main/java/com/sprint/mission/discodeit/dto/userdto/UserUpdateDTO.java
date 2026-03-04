package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;

import java.time.Instant;
import java.util.UUID;

public record UserUpdateDTO(
    String newUsername,
    String newEmail,
    String newPassword

) {

}
