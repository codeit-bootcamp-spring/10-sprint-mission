package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UserUpdateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID userId,
        String email,
        String password,
        String userName,
        String nickName,
        String birthday,
        BinaryContentCreateRequest profileImage
) {
}
