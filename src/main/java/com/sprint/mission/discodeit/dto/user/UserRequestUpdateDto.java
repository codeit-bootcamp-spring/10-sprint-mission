package com.sprint.mission.discodeit.dto.user;
import java.util.UUID;

public record UserRequestUpdateDto(UUID id,
                                   String userName,
                                   String userEmail,
                                   String userPassword,
                                   Boolean online) {
}
