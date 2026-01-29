package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;



public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "users.dat";
    private Map<UUID, User> data;

    public FileUserRepository() {
        this.data = loadFromFile();
    }

    @Override
    public void save (User user) {
        data.put(user.getId(), user);
        saveToFile();
    }
    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }
    @Override
    public Optional<User> findById(UUID id) {

        return Optional.ofNullable(data.get(id));
    }
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }



    
    private void saveToFile() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("User 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadFromFile(){
        File file = new File(FILE_PATH);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
