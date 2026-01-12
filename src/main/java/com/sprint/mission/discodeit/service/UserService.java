package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.HashSet;
import java.util.UUID;

public interface UserService {
    public User find(UUID id);
    public HashSet<User> findAll();
    public User create(User user);
    public void delete(UUID id);
    public User update(UUID id, String str);
}
