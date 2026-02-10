package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
@Repository
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository(){
        data = new HashMap<>();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public Optional<User> findByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(user-> user.getEmail().equals(email))
                .findFirst();
    }

    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    public void delete(UUID userId){
        data.remove(userId);
    }
}
