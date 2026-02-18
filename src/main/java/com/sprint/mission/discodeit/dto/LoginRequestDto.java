package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "이름은 필수입니다!")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다!")
    private String password;
}
