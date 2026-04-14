package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 유저 생성 시 필요한 데이터
public record UserCreateRequest(

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 1, max = 20, message = "사용자명은 1자 이상 20자 이하로 입력해주세요.")
    String username,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "비밀번호는 영문, 숫자를 포함하여 8자 이상이어야 합니다.")
    String password
) {

}
