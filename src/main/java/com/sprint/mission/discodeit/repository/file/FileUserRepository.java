package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = false
)
public class FileUserRepository implements UserRepository {
    private final String filePath;
    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String directory){
        this.filePath = directory + "/user.ser";
    }

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
        File file = new File(filePath);
        if(!file.exists()) return new HashMap<>();

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID,User>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, User> data){
        File file = new File(filePath);
        if(file.getParentFile() != null && !file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(file)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
