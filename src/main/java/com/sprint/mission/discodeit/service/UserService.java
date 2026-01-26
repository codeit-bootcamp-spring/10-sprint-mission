package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
        User create(String name, String email, String password);
        User read(UUID userId);
        List<User> readAll();
        List<User> getUsersByChannel(UUID channelId);
        User update(UUID id, String name, String email, String password);
        void delete(UUID id);

    }
