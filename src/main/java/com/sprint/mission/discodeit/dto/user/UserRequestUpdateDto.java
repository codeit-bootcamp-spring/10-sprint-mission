package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.common.BinaryContentParam;

import java.util.UUID;

public record UserRequestUpdateDto(UUID id,
                                   String userName,
                                   String userEmail,
                                   String userPassword,
                                   BinaryContentParam profileImage) {
}
