package com.sprint.mission.repository;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    List<User> findByChannelId(UUID channelId);

    List<User> findAll();

//    User update(UUID id, String name);

    void deleteById(UUID id);
}
