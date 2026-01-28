package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID,User> data = new HashMap<>();


    @Override
    public User save(User user) {
        data.put(user.getId(),user);
        return data.get(user.getId());
    }

    @Override
    public User findById(UUID userId) {
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userId) {
        data.remove(userId);
    }

}
