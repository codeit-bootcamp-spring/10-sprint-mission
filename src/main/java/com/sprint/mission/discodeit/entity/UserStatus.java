package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserStatus extends BaseDomain{
    private UUID userID;

    public UserStatus(UUID userID) {
        super();
        this.userID = userID;
    }
}
