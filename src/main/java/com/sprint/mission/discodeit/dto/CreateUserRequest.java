package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "유저 이름은 필수입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식을 맞춰서 작성해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    private BinaryContentDto profileImage;

}
