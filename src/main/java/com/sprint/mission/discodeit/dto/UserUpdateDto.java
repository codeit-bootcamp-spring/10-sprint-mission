package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private String id;
    private String nickname;
    private String password;
    //파라미터 그룹화
    private byte[] profileImageBytes;
    private String profileFileName;
    private String profileContentType;
}
