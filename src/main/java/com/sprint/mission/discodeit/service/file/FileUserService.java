/*
package com.sprint.mission.discodeit.service.file;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.utils.Validation;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService , Serializable {
    private final String FILE_PATH = "users.dat";
    private Map<UUID, User> data;

    public FileUserService() {
        this.data = loadFromFile();
    }
    @Override
    public User createUser(String userName, String alias, String email, String password) {
        Validation.notBlank(userName, "이름");
        Validation.notBlank(alias, "별명");

        Validation.noDuplicate(
                data.values(),
                user->user.getAlias().equals(alias),
                "이미 존재하는 별명입니다: " + alias
        );
        User user = new User(userName, alias, email, password);
        data.put(user.getId(), user);
        saveToFile();
        return user;
    }
    @Override
    public List<User> getUserAll(){
        return new ArrayList<>(data.values());
    }
    @Override
    public User getUserByAlias(String alias) {
        return data.values().stream()
                .filter(u -> u.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 별명의 유저가 없습니다: " + alias));
    }
    @Override
    public List<User> getUserByName(String userName) {
        List<User> matches =  data.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .toList();
        if(matches.isEmpty()) {
            throw new NoSuchElementException("해당 이름의 유저가 없습니다: " + userName);
        }
        return matches;
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
        saveToFile();
    }

    @Override
    public User updateUser(UUID uuid, String newName, String newAlias) {
        User existing = findUserById(uuid);
        existing.changeUserName(newName);
        existing.changeAlias(newAlias);
        saveToFile();
        return existing;
    }

    @Override
    public void findUserById(UUID id) {
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 유저가 없습니다: " + id);
        }
        return user;
    }

    // 파일 저장
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e){
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    // 파일 로드
    private Map<UUID, User> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject(); // 형변환
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

}
*/