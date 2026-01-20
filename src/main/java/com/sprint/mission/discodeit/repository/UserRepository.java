package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.*;

public interface UserRepository {
    void save(User user);
    void delete(UUID id);
    User findById(UUID id);
    List<User> findAll();
}
