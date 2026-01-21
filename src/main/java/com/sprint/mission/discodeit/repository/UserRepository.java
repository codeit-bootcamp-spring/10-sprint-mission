package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    public void save(User user);
    public User findByID(String userID);
    public List<User> findAll();
    public List<User> load();
    public User delete(User user);
}
