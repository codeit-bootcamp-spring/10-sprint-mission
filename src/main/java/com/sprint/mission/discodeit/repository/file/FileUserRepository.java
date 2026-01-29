package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {
    private final String FILE_PATH = "users.dat";
    private final Map<UUID, User> cache;
    public FileUserRepository(){
        cache = loadData();
    }

    private Map<UUID, User> loadData(){
        File file = new File(FILE_PATH);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void save(User user) {
        cache.put(user.getId(), user);
        saveData();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}
