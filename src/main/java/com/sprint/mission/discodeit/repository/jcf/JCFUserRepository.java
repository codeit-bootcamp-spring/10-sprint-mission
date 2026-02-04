package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User save(User user){
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<User> findAll(){
        return data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID id) { data.remove(id); }
}
