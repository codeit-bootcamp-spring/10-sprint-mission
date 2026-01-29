package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusService {
    UserStatus create();
    UserStatus find(UUID Id);
    UserStatus findAll(UUID Id);
    void update();
    void delete(UUID userId);

}
