package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String name);
    User findUser(UUID userId);
    List<User> findAllUsers();
    User addFriend(UUID senderId, UUID recieverId);
    List<User> findFriends(UUID userId);
    User update(UUID userId, String newName);
    void delete(UUID userId);
    void save(User user);
}
