package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {
    private final String filePath;
    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}")String directory){
        this.filePath = directory + "/userStatus.ser";
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return loadData().values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    public Map<UUID,UserStatus> findAll(){
        return loadData();
    }


    @Override
    public void save(UserStatus userStatus) {
        Map<UUID, UserStatus> data = loadData();
        data.put(userStatus.getId(),userStatus);
        saveData(data);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, UserStatus> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = loadData();
        data.values().removeIf(userStatus -> userStatus.getUserId().equals(userId));
        saveData(data);
    }

    private Map<UUID, UserStatus> loadData(){
        File file = new File(filePath);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID,UserStatus>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, UserStatus> data){
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
