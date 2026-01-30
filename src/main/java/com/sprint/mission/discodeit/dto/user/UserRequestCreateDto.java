package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.common.ProfileImageParam;

public record UserRequestCreateDto(String userName,
                                   String userEmail,
                                   String userPassword,
                                   ProfileImageParam profileImage) {

}
