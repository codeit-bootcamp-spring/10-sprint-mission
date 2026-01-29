package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserRequestCreateDto(String userName,
                                   String userEmail,
                                   ProfileImageParam profileImage) {

}
