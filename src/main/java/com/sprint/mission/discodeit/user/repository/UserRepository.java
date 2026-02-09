package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID userId);
    Optional<User> findByName(String userName);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findAllByChannelId(UUID channelId);
    void save(User user);
    void deleteById(UUID userId);
}
