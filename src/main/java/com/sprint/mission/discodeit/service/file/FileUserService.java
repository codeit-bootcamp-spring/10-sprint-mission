package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileUserService implements UserService {

    private static final String FILE_PATH = "users.dat";

    public FileUserService() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            saveToFile(new HashSet<>());
        }
    }

    public FileUserService(boolean dummy){ //테스트할때 리셋용 더미생성자
        saveToFile(new HashSet<>());
    }

    @Override
    public User find(UUID id) {
        Set<User> usersInFile = findAll();
        return usersInFile.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));
    }

    @Override
    public Set<User> findAll() {
        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<User>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User create(String userName) throws IOException {
        User user = new User(userName);
        Set<User> usersInFile = findAll();
        usersInFile.add(user);
        saveToFile(usersInFile);

        return user;
    }

    @Override
    public void delete(UUID id) {
        Set<User> usersInFile = findAll();
        usersInFile.remove(find(id));
        saveToFile(usersInFile);
    }

    @Override
    public User update(UUID id, String newUserName) {
        Set<User> usersInFile = findAll();

        User user = usersInFile.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("User not found: id = " + id));

        usersInFile.remove(user);
        user.updateUserName(newUserName);
        usersInFile.add(user);
        saveToFile(usersInFile);


        return user;
    }

    public User update(User user) {
        Set<User> users = findAll();
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        saveToFile(users); // 파일에 쓰는 private 메서드
        return user;
    }

    private void saveToFile(Set<User> users){
        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(users);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
