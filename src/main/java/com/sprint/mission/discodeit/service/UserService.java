package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    public User find(UUID id);
    public Set<User> findAll();
    public User create(String userName);
    public void delete(UUID id);
    public User update(UUID id, String str);
}
