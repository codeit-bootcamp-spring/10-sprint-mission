package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private static UserService instance;
    // JCF를 활용하여 데이터를 저장하는 필드
    private final List<User> data;

    // data 필드를 생성자로 초기화
    private JCFUserService() {
        data = new ArrayList<User>();
    }

    // 싱글톤 패턴?
    public static UserService getInstance() {
        if (instance == null) instance = new JCFUserService();
        return instance;
    }

    //  [ ] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
    @Override
    public User create(String nickName, String userName, String email, String phoneNumber) {
        User newUser = new User(nickName, userName, email, phoneNumber);
        data.add(newUser);
        return newUser;
    }

    @Override
    public User findById(UUID id) {
        return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("id가 " + id + "인 유저를 찾을 수 없습니다."));
    }

    @Override
    public User findByUserName(String userName) {
        return data.stream()
            .filter(user -> user.getUserName().equals(userName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> findAllUser() {
        return data;
    }

    @Override
    public User updateNickName(UUID userId, String nickName) {
        User user = data.stream()
            .filter(u -> u.getId().equals(userId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(userId + "은(는) 존재하지 않는 유저ID입니다.")
            );

        user.updateNickName(nickName);
        user.updateUpdatedAt(System.currentTimeMillis());

        return user;
    }

    @Override
    public User updateUserName(UUID userId, String userName) {
        // userName(사용자명)은 중복이 될 수 없다.
        if (data.stream()
            .anyMatch(u -> u.getUserName().equals(userName))) {
            throw new IllegalArgumentException(userName + "은(는) 이미 존재하는 사용자명입니다.");
        }

        User user = data.stream()
            .filter(u -> u.getId().equals(userId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(userId + "은(는) 존재하지 않는 유저ID입니다.")
            );

        user.updateUserName(userName);
        user.updateUpdatedAt(System.currentTimeMillis());

        return user;
    }

    @Override
    public User updateEmail(UUID userId, String email) {
        User user = data.stream()
            .filter(u -> u.getId().equals(userId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(userId + "은(는) 존재하지 않는 유저ID입니다.")
            );

        user.updateEmail(email);
        user.updateUpdatedAt(System.currentTimeMillis());

        return user;
    }

    @Override
    public User updatePhoneNumber(UUID userId, String phoneNumber) {
        User user = data.stream()
            .filter(u -> u.getId().equals(userId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(userId + "은(는) 존재하지 않는 유저ID입니다.")
            );

        user.updateEmail(phoneNumber);
        user.updateUpdatedAt(System.currentTimeMillis());

        return user;
    }

    @Override
    public boolean delete(UUID id) {
        return data.removeIf(user -> user.getId().equals(id));
    }
}
