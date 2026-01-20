package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserRepository<K,V> {

    public User create(User user);
    public User findById(UUID id);
    public List<User> findAll();
    public User update();
    public void delete(UUID id);
}
