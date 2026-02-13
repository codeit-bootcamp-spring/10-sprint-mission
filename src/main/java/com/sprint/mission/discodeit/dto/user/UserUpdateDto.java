package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.Email;


import java.util.UUID;

public record UserUpdateDto(
        String username,
        //@Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        String password
) {
}
