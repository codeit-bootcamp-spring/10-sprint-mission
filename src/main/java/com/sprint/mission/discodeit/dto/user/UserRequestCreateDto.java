package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.common.BinaryContentParam;

public record UserRequestCreateDto(String userName,
                                   String userEmail,
                                   String userPassword,
                                   BinaryContentParam profileImage) {

}
