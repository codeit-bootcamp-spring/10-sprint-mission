package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatusType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserCreateResponseDTO {
    private UUID id;
    private String email;
    private String nickname;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID profileId;

    private UserStatusType status;
}