package com.sprint.mission.discodeit.dto.authdto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "유저 이름은 공백이 포함되거나 Null일 수 없습니다.")
    String username,

    @NotBlank(message = "비밀번호는 공백이 포함되거나 Null일 수 없습니다.")
    String password
) {

}
