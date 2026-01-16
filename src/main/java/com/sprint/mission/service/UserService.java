package com.sprint.mission.service;

import com.sprint.mission.entity.User;

public interface UserService extends BaseService<User> {
    User create(String userName);
}
