package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    public User find(UUID id);
    public Set<User> findAll();
    public User create(String userName) throws IOException;
    public void delete(UUID id);
    public User update(UUID id, String newUserName);
    public User update(UUID id, List<Role> roles);
}
