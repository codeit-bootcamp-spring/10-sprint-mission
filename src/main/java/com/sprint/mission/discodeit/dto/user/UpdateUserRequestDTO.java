package com.sprint.mission.discodeit.dto.user;

// 비어있으면 해당 필드는 업데이트 안 함
import jakarta.validation.constraints.*;

public record UpdateUserRequestDTO(

        @Size(min = 2, max = 20, message = "username은 2~20자여야 합니다.")
        String newUsername,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "email은 100자 이하여야 합니다.")
        String newEmail,

        @Size(min = 8, max = 20, message = "password는 8~20자여야 합니다.")
        String newPassword
) { }