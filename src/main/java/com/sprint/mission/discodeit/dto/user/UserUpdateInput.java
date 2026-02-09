package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.input.BinaryContentCreateInput;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID userId,
        String email,
        String password,
        String userName,
        String nickName,
        String birthday,
        BinaryContentCreateInput profileImage
) {
}
