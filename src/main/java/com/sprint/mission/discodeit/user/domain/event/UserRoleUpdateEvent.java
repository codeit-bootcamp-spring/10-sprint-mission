package com.sprint.mission.discodeit.user.domain.event;

import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.UUID;

public record UserRoleUpdateEvent(UUID id,
                                  UserRole oldRole,
                                  UserRole newRole) {

}
