package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.HashSet;
import java.util.UUID;

public interface UserService {
    public void read(UUID id);
    public void readAll();
    public User create(User user);
    public void delete(User user);
    public void update(User originuser, User afteruser);
}
