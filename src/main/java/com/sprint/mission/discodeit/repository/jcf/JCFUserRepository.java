package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;


public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();;

    @Override
    public User save(User user) {
       this.data.put(user.getId(), user);
        return user;
    }

    @Override
   public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<User> findByUsername(String name) {
        return data.values().stream()
                .filter(u -> u.getUsername().equals(name))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

     @Override
   public void deleteById(UUID id) {
       this.data.remove(id);
   }
}
