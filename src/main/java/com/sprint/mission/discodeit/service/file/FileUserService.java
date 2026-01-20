package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final String FILE_PATH = "./users.ser";

    // 파일 쓰기 (직렬화)
    private void saveData(Map<UUID, User> data){
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){

            oos.writeObject(data);

        } catch (IOException e){
            System.err.println("[파일 저장 실패]: " + e.getMessage());
        }
    }

    // 파일 읽기 (역직렬화)
    private Map<UUID, User> loadData(){
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)){

            return (Map<UUID, User>) ois.readObject();

        } catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }


    // 유저 생성
    @Override
    public User create(String name, String nickname, String email, String password){
        Map<UUID, User> userData = loadData();

        User newUser = new User(name, nickname, email, password);
        userData.put(newUser.getId(), newUser);

        saveData(userData);
        return newUser;
    }

    // 유저 ID로 조회
    @Override
    public User findById(UUID id){
        Map<UUID, User> userData = loadData();

        User user = userData.get(id);
        if (user == null){
            throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
        }
        return user;
    }

    // 유저 전부 조회
    @Override
    public List<User> findAll(){
        Map<UUID, User> userData = loadData();

        return new ArrayList<>(userData.values());
    }

    // 유저 정보 수정
    @Override
    public User update(UUID id, String name, String nickname, String email, String status, String password) {
        Map<UUID, User> userData = loadData();

        User user = userData.get(id);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
        }

        Optional.ofNullable(name).ifPresent(user::updateName);
        Optional.ofNullable(nickname).ifPresent(user::updateNickname);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(status).ifPresent(user::updateStatus);
        Optional.ofNullable(password).ifPresent(user::updatePassword);

        saveData(userData);
        return user;
    }

    // 유저 삭제
    @Override
    public void delete(UUID id){
        Map<UUID, User> userData = loadData();

        User user = userData.get(id);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
        }

        userData.remove(id);
        saveData(userData);
    }

    // 특정 유저가 참가한 채널 목록 조회
    @Override
    public List<Channel> findJoinedChannelsByUserId(UUID userId){
        Map<UUID, User> userData = loadData();

        User user = userData.get(userId);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
        }

        return user.getJoinedChannels();
    }

    // 특정 유저가 발행한 메시지 목록 조회
    @Override
    public List<Message> findMessagesByUserId(UUID userId){
        Map<UUID, User> userData = loadData();

        User user = userData.get(userId);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
        }

        return user.getMyMessages();
    }

    // 다른 FileService에서 유저 파일을 사용 수 있도록 하기 위한 메서드
    public void update(User user) {
        Map<UUID, User> userData = loadData();
        userData.put(user.getId(), user);
        saveData(userData);
    }
}
