package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthLoginRequestDTO {
    private UUID userId;
    private String nickname;
    private String password;
}
