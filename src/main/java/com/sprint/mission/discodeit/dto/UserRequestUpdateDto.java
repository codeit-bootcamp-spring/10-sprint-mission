package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserRequestUpdateDto(UUID id,
                                   String userName,
                                   String userEmail,
                                   UserRequestCreateDto.ProfileImageParam profileImage) {
}
