package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileUserRepository extends BaseFileRepository<User> implements UserRepository {
    public FileUserRepository() {
        super("users.ser");
    }

    @Override
    public User save(User user){
        Map<UUID, User> data = loadData();
        data.put(user.getId(), user);
        saveData(data);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id){
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return loadData().values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<User> findByName(String name) {
        return loadData().values().stream()
                .filter(user -> name.equals(user.getName()))
                .findFirst();
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(User user){
        Map<UUID, User> data = loadData();
        data.remove(user.getId());
        saveData(data);
    }
}
