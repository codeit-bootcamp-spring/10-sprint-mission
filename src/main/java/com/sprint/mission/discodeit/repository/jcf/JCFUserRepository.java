package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();

    @Override
    public User save(User user) {
        delete(user);
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(User user) {
        data.removeIf(u -> u.equals(user));
    }
}
