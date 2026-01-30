package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "users.dat";

    private Map<UUID, User> loadUserFile(){
        File file = new File(FILE_PATH);
        if (!file.exists()){
            return new HashMap<>();
        } try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, User>) ois.readObject();
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserFile(Map<UUID, User> users){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void resetUserFile(){
        saveUserFile(new LinkedHashMap<>());
    }

    @Override
    public User save(User user) {
        Map<UUID, User> users = loadUserFile();
        users.put(user.getId(), user);
        saveUserFile(users);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(loadUserFile().get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(loadUserFile().values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> users = loadUserFile();
        User removed = users.remove(id);
        if (removed == null) throw new UserNotFoundException();
        saveUserFile(users);
    }

    @Override
    public boolean existsByEmail(String email) {
        return loadUserFile().values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean existsByName(String name) {
        return loadUserFile().values().stream()
                .anyMatch(user -> user.getName().equals(name));
    }
}