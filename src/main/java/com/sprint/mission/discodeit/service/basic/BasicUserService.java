package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email, String profileImageUrl) {
        Objects.requireNonNull(name, "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(email, "이메일은 필수 항목입니다.");
        Objects.requireNonNull(profileImageUrl, "프로필 이미지 URL은 필수 항목입니다.");

        User user = new User(name, email, profileImageUrl);
        userRepository.save(user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String name, String email, String profileImageUrl) {
        User user = findById(userId);
        Optional.ofNullable(name).ifPresent(user::updateName);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(profileImageUrl).ifPresent(user::updateProfileImageUrl);

        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        findById(userId); // 존재 여부 확인
        userRepository.delete(userId);
    }
}