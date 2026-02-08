package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    /*
    * [ ] username, password과 일치하는 유저가 있는지 확인합니다.
[ ] 일치하는 유저가 있는 경우: 유저 정보 반환
[ ] 일치하는 유저가 없는 경우: 예외 발생
[ ] DTO를 활용해 파라미터를 그룹화합니다.*/
    public record Request(
            @NotBlank
            String username,

            @NotBlank
            String password
    ) {}


}
