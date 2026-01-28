package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceType;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private static BasicUserService instance;

    private final UserRepository userRepository;

    private BasicUserService(ServiceType type) {
        if (type == ServiceType.JCF) {
            userRepository = JCFUserRepository.getInstance();
        } else {
            userRepository = FileUserRepository.getInstance();
        }
    }

    public static UserService getInstance(ServiceType type) {
        if (instance == null) instance = new BasicUserService(type);
        return instance;
    }

    @Override
    public User create(String nickName, String userName, String email, String phoneNumber) {
        return userRepository.save(
            new User(nickName, userName, email, phoneNumber)
        );
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("id가 " + id + "인 유저를 찾을 수 없습니다."));
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
            .orElseThrow(
                () -> new NoSuchElementException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다.")
            );
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID userId, User user) {
        User updatedUser = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다."));
        ;

        // input이 null이 아닌 필드 업데이트
        Optional.ofNullable(user.getNickName())
            .ifPresent(updatedUser::updateNickName);
        Optional.ofNullable(user.getUserName())
            .ifPresent(updatedUser::updateUserName);
        Optional.ofNullable(user.getEmail())
            .ifPresent(updatedUser::updateEmail);
        Optional.ofNullable(user.getPhoneNumber())
            .ifPresent(updatedUser::updatePhoneNumber);

        userRepository.save(updatedUser);

        return updatedUser;
    }

    @Override
    public void delete(UUID userId) {
        userRepository.delete(userId);
    }
}
