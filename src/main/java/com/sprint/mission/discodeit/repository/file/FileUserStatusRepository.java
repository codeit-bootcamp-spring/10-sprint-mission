package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    // 필드
    private static final String BASE_PATH = "data/userStatus";
    private static final String STORE_FILE = BASE_PATH + "/userStatus.ser";
    private List<UserStatus> userStatusData;

    public FileUserStatusRepository() {
        init();
        loadData();
    }

    private void init() {
        try {
            if (!Files.exists(Paths.get(BASE_PATH))) {
                Files.createDirectories(Paths.get(BASE_PATH));
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    private void loadData(){
        if(!Files.exists(Paths.get(STORE_FILE))){
            userStatusData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_FILE))){
            userStatusData = (List<UserStatus>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    private void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE))){
            oos.writeObject(userStatusData);
        } catch (IOException e){
            throw new RuntimeException("Data save failed." + e.getMessage());
        }
    }
    @Override
    public Optional<UserStatus> find(UUID userStatusID) {
        loadData();
        return userStatusData.stream()
                .filter(userStatus -> userStatus.getId().equals(userStatusID))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserID(UUID userID) {
        loadData();
        return userStatusData.stream()
                .filter(userStatus -> userStatus.getUserID().equals(userID))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        loadData();
        return new ArrayList<>(userStatusData);
    }

    @Override
    public void deleteUserStatus(UUID userStatusID) {
        loadData();
        userStatusData.removeIf(userStatus -> userStatus.getId().equals(userStatusID));
        saveData();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        loadData();
        for(int i=0; i<userStatusData.size(); i++){
            if(userStatusData.get(i).getId().equals(userStatus.getId())){
                userStatusData.set(i, userStatus);
                saveData();
                return userStatus;
            }
        }
        userStatusData.add(userStatus);
        saveData();
        return userStatus;
    }
}
