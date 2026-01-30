package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {
    private static final String USER_FILE = "data/user.ser";

    @Override
    public Optional<User> findById(UUID userId) {
        Map<UUID , User> data = loadData();

        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public Optional<User> findByName(String name) {
        return loadData().values().stream()
                .filter(user-> user.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(User user) {
        Map<UUID, User> data = loadData();
        data.put(user.getId(),user);
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
