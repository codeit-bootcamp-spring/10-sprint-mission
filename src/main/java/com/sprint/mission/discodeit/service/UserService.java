package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password, UUID profileId);

    User findById(UUID id);
    List<User> findAll();
    List<User> findUsersByChannelId(UUID channelId);

    User update(UUID id, String username, String email, String password, UUID profileId);

    void deleteUserByUserId(UUID id);

    void save(User user);
}
