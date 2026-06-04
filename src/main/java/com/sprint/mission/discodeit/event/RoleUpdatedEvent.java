package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.Role;
import lombok.Getter;

import java.util.UUID;


@Getter
public class RoleUpdatedEvent {

    private final UUID receiverId;
    private final Role oldRole;
    private final Role newRole;

    public RoleUpdatedEvent(UUID receiverId, Role oldRole, Role newRole){
        this.receiverId = receiverId;
        this.oldRole = oldRole;
        this.newRole = newRole;
    }
}
