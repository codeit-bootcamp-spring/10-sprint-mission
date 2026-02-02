package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private static final String USER_STATUS_FILE = "data/userStatus.ser";

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
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_STATUS_FILE))){
            return (Map<UUID,UserStatus>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, UserStatus> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(USER_STATUS_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
