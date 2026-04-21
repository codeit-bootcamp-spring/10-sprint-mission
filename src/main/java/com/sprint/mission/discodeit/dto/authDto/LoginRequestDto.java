package com.sprint.mission.discodeit.dto.authDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @NotNull(message = "유저 이름은 필수입니다")
    private String username;

    @NotNull(message = "비밀번호는 필수입니다")
    private String password;
}
