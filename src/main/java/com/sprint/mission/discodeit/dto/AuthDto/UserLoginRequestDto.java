package com.sprint.mission.discodeit.dto.AuthDto;

import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String name;
    private String email;
    private String password;
}
