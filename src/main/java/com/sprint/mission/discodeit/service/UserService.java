package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserServiceDTO.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserPresence;

import java.util.List;

public interface UserService extends DomainService<User, UserCreation, UserInfoUpdate> {
    User find(UserFinding model);
    List<User> findAll(UserPresence presence);
}
