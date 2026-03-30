package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;

// 로그인 시 필요한 데이터
public record LoginRequest(

    @NotBlank(message = "사용자명을 입력하세요.")
    String username,

    @NotBlank(message = "비밀번호를 입력하세요.")
    String password
) {

}
