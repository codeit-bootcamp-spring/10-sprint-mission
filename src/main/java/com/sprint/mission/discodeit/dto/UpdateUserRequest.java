package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;      // null이면 업데이트 안함
    private String email;         // null이면 업데이트 안함
    private String password;      // null이면 업데이트 안함
    private ProfileImageData profileImage;  // null이면 업데이트 안함
}
