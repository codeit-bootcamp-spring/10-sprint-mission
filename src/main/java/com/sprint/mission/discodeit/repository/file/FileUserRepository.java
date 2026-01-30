package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

    public FileUserRepository() {
        super("User.ser");
    }
    @Override
    public User save(User user) {
        Map<UUID, User> data = load();
        data.put(user.getId(), user);
        writeToFile(data);
        return user;
    }
    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = load();
        data.remove(id);
        writeToFile(data);
    }
    @Override
    public Optional<User> findById(UUID id) {
        Map<UUID, User> data = load();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByName(String name) {
        Map<UUID, User> data = load();
        Optional<User> user = data.values().stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email){
        Map<UUID, User> data = load();
        Optional<User> user = data.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
        return user;
    }

    @Override
    public List<User> readAll() {
        Map<UUID, User> data = load();
        return List.copyOf(data.values());
    }
    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, User>());
    }

}
