package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final File file = new File("users.dat");
    private  Map<UUID, User> userStore;

    public FileUserRepository(){
        userStore = new HashMap<>();
        loadFile();
    }

    private void loadFile() {
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<UUID, User> loaded = (Map<UUID, User>) ois.readObject();
            userStore.clear();
            userStore.putAll(loaded);
        } catch (Exception e) {
            throw new RuntimeException("유저 파일 로드 실패",e);
        }
    }

    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(userStore);
        } catch (IOException e) {
            throw new RuntimeException("유저 파일 저장 실패",e);
        }
    }

    public User save (User user){
        userStore.put(user.getId(), user);
        saveFile();
        return user;
    }

    public User findById(UUID id) {
        User user = userStore.get(id);
        if(user == null){
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다");
        }
        return user;
    }

    public List<User> findAll(){
        return new ArrayList<>(userStore.values());
    }

    public void delete(UUID id) {
        userStore.remove(id);
        saveFile();
    }
}
