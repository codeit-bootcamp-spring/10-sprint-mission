package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserRequestDto {
    private String username;
    private String email;
    private String password;
    private BinaryContentDto newProfileImage;
}
