package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserCreateDto {
    private String username;
    private String email;
    private String password;
    //파라미터 그룹화
    private final BinaryContentDto profileImage;
}
