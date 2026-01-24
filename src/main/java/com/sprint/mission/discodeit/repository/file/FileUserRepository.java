package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static FileUserRepository instance = null;
    public static FileUserRepository getInstance(){
        if(instance == null){
            instance = new FileUserRepository();
        }
        return instance;
    }
    private FileUserRepository(){}
    private static final String FILE_PATH = "users.dat";

    @Override
    public void fileSave(Set<User> users) {
        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(users);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<User> fileLoad() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashSet<>(); // 파일이 없으면 빈 셋 반환
        }

        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<User>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fileDelete(UUID id) {
        Set<User> users = fileLoad();
        users.removeIf(role -> role.getId().equals(id));
        fileSave(users);
    }
}
