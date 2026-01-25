package com.sprint.mission.discodeit.service.file;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final String FILE_PATH = "user.dat";

    // 객체 불러오기
    private List<User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 객체 저장하기
    private void saveData(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드
    public User CreateUser(String userName, String email) {
        List<User> data = loadData();
        
        if (data.stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 등록된 계정입니다. " + email);
        }

        User newUser = new User(userName, email);
        data.add(newUser);
        saveData(data);
        
        System.out.println(newUser.getUserName() + "님의 계정 생성이 완료되었습니다.");
        return newUser;
    }

    public User findById(UUID userId) {
        List<User> data = loadData(); // 조회 시에도 최신 파일을 읽어옵니다.
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 계정은 존재하지 않습니다.\nID: " + userId));
    }

    public User findByEmail(String email) {
        List<User> data = loadData();
        return data.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 이메일이 등록되어 있지 않습니다."));
    }


    public List<User> findAll() {
        return loadData();

    }

    public List<User> findUsersByChannel(UUID channelId) {
        List<User> data = loadData();
        return data.stream()
                .filter(user -> user.getChannelList().stream().anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    public User update(UUID userId, String userName, String email) {
        List<User> data = loadData();
        User foundUser = data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        foundUser.update(userId, userName, email);
        saveData(data);
        return foundUser;
    }


    public void addMessage(UUID userId, Message msg) {
        List<User> data = loadData();
        User foundUser = findById(userId);
        foundUser.addMessage(msg);

        saveData(data);
    }

    public void addChannel(UUID userId, Channel channel) {
        List<User> data = loadData();
        User foundUser = findById(userId);
        foundUser.addChannel(channel);
        channel.addUserList(foundUser);

        saveData(data);
    }


    public void delete(UUID userId) {
        List<User> data = loadData();
        User target = findById(userId);
        data.remove(target);
        saveData(data);
    }

    @Override
    public void deleteAll() {
        List<User> data = new ArrayList<>();
        saveData(data);
        System.out.println("모든 유저 데이터를 초기화했습니다.");
    }

}