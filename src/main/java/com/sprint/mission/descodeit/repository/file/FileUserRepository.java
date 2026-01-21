package com.sprint.mission.descodeit.repository.file;

import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static final String USER_FILE = "data/user.ser";

    public FileUserRepository(){

    }
    @Override
    public User findById(UUID userId) {
        Map<UUID , User> data = loadData();

        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(UUID userId, User user) {
        Map<UUID, User> data = loadData();
        data.put(userId,user);
        saveData(data);
    }

    @Override
    public void delete(UUID userId) {
        Map<UUID, User> data = loadData();
        data.remove(userId);
        saveData(data);
    }

    private Map<UUID, User> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))){
            return (Map<UUID,User>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, User> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(USER_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
