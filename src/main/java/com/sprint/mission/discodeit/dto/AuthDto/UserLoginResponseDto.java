package com.sprint.mission.discodeit.dto.AuthDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserLoginResponseDto {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private UUID profileId;
}
