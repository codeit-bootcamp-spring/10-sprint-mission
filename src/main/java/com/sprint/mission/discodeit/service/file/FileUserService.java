package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {
    public static final Path USER_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "users");

    private static UserService instance;
    private final FileIo<User> fileIo;


    private FileUserService() {
        this.fileIo = new FileIo<>(USER_DIRECTORY);
        this.fileIo.init();
    }

    public static UserService getInstance() {
        if (instance == null) instance = new FileUserService();
        return instance;
    }


    @Override
    public User create(String nickName, String userName, String email, String phoneNumber) {
        User newUser = new User(nickName, userName, email, phoneNumber);

        List<User> users = fileIo.load();
        users.add(newUser);
        fileIo.save(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public User findById(UUID id) {
        return fileIo.load().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + id + "인 유저를 찾을 수 없습니다.")
            );
    }

    @Override
    public User findByUserName(String userName) {
        return fileIo.load().stream()
            .filter(u -> u.getUserName().equals(userName))
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다.")
            );
    }

    @Override
    public List<User> findAllUser() {
        return fileIo.load();
    }

    @Override
    public User updateUser(UUID userId, User user) {
        User updatedUser = this.findById(userId);

        // input이 null이 아닌 필드 업데이트
        Optional.ofNullable(user.getNickName())
            .ifPresent(updatedUser::updateNickName);
        Optional.ofNullable(user.getUserName())
            .ifPresent(updatedUser::updateUserName);
        Optional.ofNullable(user.getEmail())
            .ifPresent(updatedUser::updateEmail);
        Optional.ofNullable(user.getPhoneNumber())
            .ifPresent(updatedUser::updatePhoneNumber);

        fileIo.save(userId, updatedUser);

        return updatedUser;
    }

    @Override
    public void delete(UUID userId) {
        try {
            fileIo.delete(userId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
