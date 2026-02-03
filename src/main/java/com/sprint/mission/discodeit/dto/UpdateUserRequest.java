package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String email;
    private String password;
    private BinaryContentDto newProfileImage;
}
