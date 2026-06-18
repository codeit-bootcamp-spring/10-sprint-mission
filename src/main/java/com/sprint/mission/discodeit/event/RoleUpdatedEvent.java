package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class RoleUpdatedEvent {
    UUID userId;
    /// 이전 권한
    Role previousRole;
    /// 새로운 권한
    Role newRole;

    UserDto user;
}
